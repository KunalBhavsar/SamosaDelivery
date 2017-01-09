package co.rapiddelivery.src;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.adapters.DeliveryAdapter;
import co.rapiddelivery.adapters.PickUpAdapter;
import co.rapiddelivery.intf.OnDialogClickListener;
import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.network.APIClient;
import co.rapiddelivery.network.DeliveryResponseModel;
import co.rapiddelivery.network.LoginResponse;
import co.rapiddelivery.utils.ActivityUtils;
import co.rapiddelivery.receiver.AlarmReceiver;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.utils.SPrefUtils;
import co.rapiddelivery.views.CustomTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabActivity extends AppCompatActivity {

    private static final String TAG = TabActivity.class.getSimpleName();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private Activity mActivityContext;
    private Context mAppContext;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        loadingDialog = LoadingDialog.getInstance();
        mActivityContext = this;
        mAppContext = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_logout:
                onLogoutClicked();
                return true;
            case R.id.action_search:
                // TODO: 9/1/17 search
                return true;
            case R.id.action_filter:
                onFilterClick();
                return true;
            case R.id.action_switch_to_map:
                Intent intent = new Intent(TabActivity.this, MapsActivity.class);
                TabActivity.this.startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onFilterClick() {
        String[] type = new String[]{"COD", "DELIVERED"};
        List<String> filterList = new ArrayList<>();
        filterList.addAll(Arrays.asList(type));
        ActivityUtils.showFilterDialog(this, filterList, new OnDialogClickListener() {
            @Override
            public void onClick(String filterType) {
                // TODO: 9/1/17 perform filtering
            }
        });
    }

    private void onLogoutClicked() {
        SPrefUtils.setIntegerPreference(mAppContext, SPrefUtils.LOGIN_STATUS, KeyConstants.LOGIN_STATUS_BLANK);
        SPrefUtils.setStringPreference(mAppContext, SPrefUtils.LOGGEDIN_USER_DETAILS, null);
        RDApplication.setAppOwnerData(null);
        RDApplication.setDeliveryModels(null);
        RDApplication.setPickupSetModel(null);
        Intent intent = new Intent(mActivityContext, LoginActivity.class);
        mActivityContext.startActivity(intent);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.cancelAlarm(getApplicationContext());
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private RecyclerView recyclerView;
        private ProgressBar loadingProgress;
        private CustomTextView txtComingSoon;
        private CustomTextView txtRetry;

        private DeliveryAdapter deliveryAdapter;
        private PickUpAdapter pickUpAdapter;
        private int sectionNumber;

        private Context mContext;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            loadingProgress = (ProgressBar) rootView.findViewById(R.id.loading_progress);
            txtComingSoon = (CustomTextView) rootView.findViewById(R.id.txt_coming_soon);
            txtRetry = (CustomTextView) rootView.findViewById(R.id.txt_reload_data);

            mContext = this.getContext();

            sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            if (sectionNumber == 1) {
                deliveryAdapter = new DeliveryAdapter(this.getContext(), RDApplication.getDeliveryModels(), new DeliveryAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(DeliveryModel deliveryModel) {
                        Intent intent = new Intent(getActivity(), DeliveryDetailsActivity.class);
                        intent.putExtra(KeyConstants.INTENT_EXTRA_DELIVERY_NUMBER, deliveryModel.getDeliveryNumber());
                        intent.putExtra(KeyConstants.INTENT_EXTRA_SHIPMENT_AWB, deliveryModel.getAwb());
                        getActivity().startActivity(intent);
                    }
                });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(deliveryAdapter);

                txtRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDRListFromServer();
                    }
                });
            }
            else if (sectionNumber == 2) {
                txtComingSoon.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                txtRetry.setVisibility(View.GONE);

                pickUpAdapter = new PickUpAdapter(this.getContext(), RDApplication.getPickupSetModel().getPickupSetModels(), new PickUpAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(PickUpModel pickUpModel) {
                        Intent intent = new Intent(getActivity(), PickUpDetailsActivity.class);
                        intent.putExtra(KeyConstants.INTENT_EXTRA_PICKUP_NUMBER, pickUpModel.getPickupNumber());
                        getActivity().startActivity(intent);
                    }
                });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(pickUpAdapter);
            }

            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (sectionNumber == 1) {
                getDRListFromServer();
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onDeliveryDataUpdatedEvent(RDApplication.DeliveryDataUpdatedEvent event) {
            Toast.makeText(getActivity(), "Delivery data updated", Toast.LENGTH_SHORT).show();
            if (sectionNumber == 1) {
                deliveryAdapter.setDeliveryList(event.deliveryModels);
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onPickupDataUpdatedEvent(RDApplication.PickupDataUpdatedEvent event) {
            Toast.makeText(getActivity(), "Pickup data updated", Toast.LENGTH_SHORT).show();
            if (sectionNumber == 2) {
                pickUpAdapter.setPickUpModelList(event.pickupSetModel.getPickupSetModels());
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            EventBus.getDefault().unregister(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            EventBus.getDefault().register(this);
            if (sectionNumber == 1) {
                deliveryAdapter.setDeliveryList(RDApplication.getDeliveryModels());
            }
            if (sectionNumber == 2) {
                pickUpAdapter.setPickUpModelList(RDApplication.getPickupSetModel().getPickupSetModels());
            }
        }

        private void showProgress(boolean showProgress) {
            if (showProgress) {
                loadingProgress.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                txtComingSoon.setVisibility(View.GONE);
                txtRetry.setVisibility(View.GONE);
            }
            else {
                loadingProgress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                txtComingSoon.setVisibility(View.GONE);
                txtRetry.setVisibility(View.GONE);
            }
        }

        public void getDRListFromServer() {
            if (ActivityUtils.isNetworkConnected(this.getContext())) {
                showProgress(true);
                LoginResponse loginResponse = RDApplication.getAppOwnerData();

                APIClient.getClient().getDeliveryList(loginResponse.getUserName(), loginResponse.getPassword(), loginResponse.getEmp_id())
                .enqueue(new Callback<DeliveryResponseModel>() {
                    @Override
                    public void onResponse(Call<DeliveryResponseModel> call, Response<DeliveryResponseModel> response) {
                        DeliveryResponseModel responseModel = response.body();
                        if(null != responseModel) {
                            switch (responseModel.getStatusCode()) {
                                case "200":
                                    List<DeliveryModel> deliveryModels = new ArrayList<>();
                                    DeliveryModel deliveryModel;
                                    for (DeliveryResponseModel.DeliveryModel deliveryModelFromServer : response.body().getDelivery()) {
                                        deliveryModel = new DeliveryModel();
                                        deliveryModel.setHeader(true);
                                        deliveryModel.setDeliveryNumber(deliveryModelFromServer.getDispatch_number());
                                        deliveryModels.add(deliveryModel);
                                        for (DeliveryResponseModel.DeliveryModel.ShipmentModel shipmentModel : deliveryModelFromServer.getShipments()) {
                                            deliveryModel = new DeliveryModel();
                                            deliveryModel.setPincode(shipmentModel.getPincode());
                                            deliveryModel.setName(shipmentModel.getName());
                                            deliveryModel.setAddress1(shipmentModel.getAddress_1());
                                            deliveryModel.setAddress2(shipmentModel.getAddress_2());
                                            deliveryModel.setAwb(shipmentModel.getAwb());
                                            deliveryModel.setDispatchCount(shipmentModel.getDispatch_count());
                                            deliveryModel.setFlow(shipmentModel.getFlow());
                                            deliveryModel.setLat(shipmentModel.getLat());
                                            deliveryModel.setLng(shipmentModel.getLng());
                                            deliveryModel.setStatus(shipmentModel.getStatus());
                                            deliveryModel.setValue(shipmentModel.getValue());
                                            deliveryModel.setMode(shipmentModel.getMode());
                                            deliveryModel.setHeader(false);
                                            deliveryModel.setDeliveryNumber(deliveryModelFromServer.getDispatch_number());
                                            deliveryModels.add(deliveryModel);
                                        }
                                    }
                                    showProgress(false);
                                    RDApplication.setDeliveryModels(deliveryModels);
                                    break;
                                default:
                                    showProgress(false);
                                    Toast.makeText(mContext, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    showRetryOption();
                                    break;
                            }
                        } else {
                            showProgress(false);
                            showRetryOption();
                            Toast.makeText(mContext, "Error in loading Delivery list", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DeliveryResponseModel> call, Throwable t) {
                        t.printStackTrace();
                        showProgress(false);
                        Toast.makeText(mContext, "Error in loading Delivery list", Toast.LENGTH_SHORT).show();
                        showRetryOption();
                    }
                });
            }
            else {
                Toast.makeText(mContext, "Check your internet connection", Toast.LENGTH_SHORT).show();
                showRetryOption();
            }
        }

        private void showRetryOption() {
            loadingProgress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            txtComingSoon.setVisibility(View.GONE);
            txtRetry.setVisibility(View.VISIBLE);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "DELIVERY";
                case 1:
                    return "PICK UP";
            }
            return null;
        }
    }
}
