package co.rapiddelivery.src;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.adapters.DeliveryAdapter;
import co.rapiddelivery.adapters.PickUpAdapter;
import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.network.APIClient;
import co.rapiddelivery.network.DeliveryResponseModel;
import co.rapiddelivery.network.LoginResponse;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.utils.SPrefUtils;
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

        getDRListFromServer();

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
        if (id == R.id.action_logout) {
            onLogoutClicked();
            return true;
        }
        if (id == R.id.action_switch_to_map) {
            Intent intent = new Intent(TabActivity.this, MapsActivity.class);
            TabActivity.this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onLogoutClicked() {
        SPrefUtils.setIntegerPreference(mAppContext, SPrefUtils.LOGIN_STATUS, KeyConstants.LOGIN_STATUS_BLANK);
        SPrefUtils.setStringPreference(mAppContext, SPrefUtils.LOGGEDIN_USER_DETAILS, null);
        RDApplication.setAppOwnerData(null);
        RDApplication.setDeliveryModels(null);
        RDApplication.setPickupSetModel(null);
        Intent intent = new Intent(mActivityContext, LoginActivity.class);
        mActivityContext.startActivity(intent);
        finish();
    }

    public void getDRListFromServer() {

        String loginDetails = SPrefUtils.getStringPreference(this, SPrefUtils.LOGGEDIN_USER_DETAILS);
        LoginResponse loginResponse = new Gson().fromJson(loginDetails, LoginResponse.class);

        APIClient.getClient().getList(loginResponse.getUserName(), loginResponse.getPassword(), Integer.parseInt(loginResponse.getEmp_id()))
            .enqueue(new Callback<DeliveryResponseModel>() {
            @Override
            public void onResponse(Call<DeliveryResponseModel> call, Response<DeliveryResponseModel> response) {
                List<DeliveryModel> deliveryModels = new ArrayList<>();
                if (response != null) {
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
                }
                RDApplication.setDeliveryModels(deliveryModels);
            }

            @Override
            public void onFailure(Call<DeliveryResponseModel> call, Throwable t) {
                t.printStackTrace();
                for (int i = 0; i < 6; i++) {
                    PickUpModel pickUpModel = new PickUpModel();
                    pickUpModel.setPickupNumber(i + "");
                    switch (i % 3) {
                        case 0 :
                            pickUpModel.setName("Kunal Bhavsar");
                            pickUpModel.setAddress("11, ABBK, Mahajan Wadi, Opp. Central Railway Workshop, Parel - Mumbai");
                            pickUpModel.setPincode("400012");
                            pickUpModel.setCutOffTime((i + 1));
                            pickUpModel.setLatitude(19.0022 + (i * 0.0001 * 19));
                            pickUpModel.setLongitude(72.8416 - (i * 0.0001 * 19));
                            break;
                        case 1 :
                            pickUpModel.setName("Shraddha Pednekar");
                            pickUpModel.setAddress("27, Jagruti Building, Devipada, near National Park, Boriwali - Mumbai");
                            pickUpModel.setPincode("400012");
                            pickUpModel.setCutOffTime((i + 1));
                            pickUpModel.setLatitude(19.0022 + (i * 0.0001 * 13));
                            pickUpModel.setLongitude(72.8416 - (i * 0.0001 * 13));
                            break;
                        case 2:
                            pickUpModel.setName("Yojana Rangnekar");
                            pickUpModel.setAddress("3, Shivaji Park, Dangal Road, Virar - Thane");
                            pickUpModel.setPincode("471012");
                            pickUpModel.setCutOffTime((i + 1));
                            pickUpModel.setLatitude(19.0022 + (i * 0.0001 * 17));
                            pickUpModel.setLongitude(72.8416 - (i * 0.0001 * 17));
                            break;
                    }
                    RDApplication.getPickupSetModel().getPickupSetModels().add(pickUpModel);
                }
                EventBus.getDefault().post(new RDApplication.PickupDataUpdatedEvent(RDApplication.getPickupSetModel()));
            }
        });
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
        private DeliveryAdapter deliveryAdapter;
        private PickUpAdapter pickUpAdapter;
        private int sectionNumber;

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
            }
            else if (sectionNumber == 2) {
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
            EventBus.getDefault().register(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            EventBus.getDefault().register(this);
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
