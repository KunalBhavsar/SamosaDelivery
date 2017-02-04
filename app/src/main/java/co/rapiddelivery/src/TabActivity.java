package co.rapiddelivery.src;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import co.rapiddelivery.network.PickupResponseModel;
import co.rapiddelivery.receiver.AlarmReceiver;
import co.rapiddelivery.utils.ActivityUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

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

    private void onLogoutClicked() {
        SPrefUtils.setIntegerPreference(mAppContext, SPrefUtils.LOGIN_STATUS, KeyConstants.LOGIN_STATUS_BLANK);
        SPrefUtils.setStringPreference(mAppContext, SPrefUtils.LOGGEDIN_USER_DETAILS, null);
        SPrefUtils.setStringPreference(mAppContext, SPrefUtils.STARTED_DELIVERY_NUMBER, null);
        RDApplication.setAppOwnerData(null);
        RDApplication.setDeliveryModels(null);
        RDApplication.setPickupModels(null);
        Intent intent = new Intent(mActivityContext, LoginActivity.class);
        mActivityContext.startActivity(intent);
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.cancelAlarm(getApplicationContext());
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements SearchView.OnQueryTextListener {
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
        private Fragment fragment;
        private String searchQuery;
        private String filterType;

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
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
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
            fragment = this;

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
                pickUpAdapter = new PickUpAdapter(this.getContext(), RDApplication.getPickupModels(), new PickUpAdapter.OnItemClickListener() {
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

                txtRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPickUpListFromServer();
                    }
                });
            }

            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            if (sectionNumber == 1) {
                getDRListFromServer();
            }
            else if (sectionNumber == 2) {
                getPickUpListFromServer();
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onDeliveryDataUpdatedEvent(RDApplication.DeliveryDataUpdatedEvent event) {
            if (sectionNumber == 1) {
                deliveryAdapter.setDeliveryList(filterDeliveries());
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onPickupDataUpdatedEvent(RDApplication.PickupDataUpdatedEvent event) {
            if (sectionNumber == 2) {
                pickUpAdapter.setPickUpModelList(filterPickups());
            }
        }

        @Override
        public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_tab, menu);

            final MenuItem searchItem = menu.findItem(R.id.action_search);

            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

            SearchView searchView = null;
            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
                searchView.setOnQueryTextListener(this);
            }
            if (searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            }

            super.onCreateOptionsMenu(menu, menuInflater);
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
                    ((TabActivity)getActivity()).onLogoutClicked();
                    return true;
                case R.id.action_search:
                    return true;
                case R.id.action_filter:
                    onFilterClick();
                    return true;
                case R.id.action_switch_to_map:
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    getActivity().startActivity(intent);
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void onFilterClick() {
            String[] type = new String[]{"ALL", "PREPAID", "COD", "REVERSE"};
            List<String> filterList = new ArrayList<>();
            filterList.addAll(Arrays.asList(type));
            ActivityUtils.showFilterDialog(getActivity(), filterList, new OnDialogClickListener() {
                @Override
                public void onClick(String filterType) {
                    updateFilterStatus(filterType);
                }
            });
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
                deliveryAdapter.setDeliveryList(filterDeliveries());
            }
            if (sectionNumber == 2) {
                pickUpAdapter.setPickUpModelList(filterPickups());
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
                                    showProgress(false);
                                    switch (responseModel.getStatusCode()) {
                                        case "200":
                                            List<DeliveryModel> deliveryModels = new ArrayList<>();
                                            List<DeliveryModel> nonDispatchedDeliveryModels = new ArrayList<>();
                                            DeliveryModel deliveryModel;
                                            for (DeliveryResponseModel.DeliveryModel deliveryModelFromServer : response.body().getDelivery()) {
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
                                                    if (deliveryModel.getStatus().equalsIgnoreCase("dispatched")) {
                                                        deliveryModels.add(deliveryModel);
                                                    }
                                                    else {
                                                        nonDispatchedDeliveryModels.add(deliveryModel);
                                                    }
                                                }
                                            }

                                            deliveryModels.addAll(nonDispatchedDeliveryModels);
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

        public void getPickUpListFromServer() {
            if (ActivityUtils.isNetworkConnected(this.getContext())) {
                showProgress(true);
                LoginResponse loginResponse = RDApplication.getAppOwnerData();

                APIClient.getClient().getPickupList(loginResponse.getUserName(), loginResponse.getPassword(), loginResponse.getEmp_id())
                        .enqueue(new Callback<PickupResponseModel>() {
                            @Override
                            public void onResponse(Call<PickupResponseModel> call, Response<PickupResponseModel> response) {
                                PickupResponseModel responseModel = response.body();
                                if(null != responseModel) {
                                    switch (responseModel.getStatusCode()) {
                                        case "200":
                                            List<PickUpModel> manifestedPickUpModels = new ArrayList<>();
                                            List<PickUpModel> dispatchedPickUpModels = new ArrayList<>();
                                            List<PickUpModel> otherPickUpModels = new ArrayList<>();
                                            PickUpModel pickUpModel;
                                            for (PickupResponseModel.PickupModel pickUpModelFromServer : response.body().getPickups()) {
                                                for (PickupResponseModel.PickupModel.RequestModel requestModel : pickUpModelFromServer.getRequests()) {
                                                    pickUpModel = new PickUpModel();
                                                    pickUpModel.setPincode(requestModel.getPincode());
                                                    pickUpModel.setName(requestModel.getName());
                                                    pickUpModel.setPhoneNumber(requestModel.getPhone());
                                                    pickUpModel.setAddress(requestModel.getAddress());
                                                    pickUpModel.setPickupNumber(requestModel.getPick_no());
                                                    pickUpModel.setExpectedCount(requestModel.getExpected_count());
                                                    pickUpModel.setLatitude(requestModel.getLat());
                                                    pickUpModel.setLongitude(requestModel.getLng());
                                                    pickUpModel.setStatus(requestModel.getStatus());
                                                    pickUpModel.setMode(requestModel.getMode());
                                                    if (pickUpModel.getStatus().equalsIgnoreCase("manifested")) {
                                                        manifestedPickUpModels.add(pickUpModel);
                                                    }
                                                    else if (pickUpModel.getStatus().equalsIgnoreCase("dispatched")) {
                                                        dispatchedPickUpModels.add(pickUpModel);
                                                    }
                                                    else {
                                                        otherPickUpModels.add(pickUpModel);
                                                    }
                                                }
                                            }

                                            manifestedPickUpModels.addAll(dispatchedPickUpModels);
                                            manifestedPickUpModels.addAll(otherPickUpModels);
                                            showProgress(false);
                                            RDApplication.setPickupModels(manifestedPickUpModels);
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
                                    Toast.makeText(mContext, "Error in loading Pickup list", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<PickupResponseModel> call, Throwable t) {
                                t.printStackTrace();
                                showProgress(false);
                                Toast.makeText(mContext, "Error in loading Pickup list", Toast.LENGTH_SHORT).show();
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

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            updateSearchText(newText);
            return true;
        }

        private void updateSearchText(String newText) {
            searchQuery = newText;
            if (sectionNumber == 1) {
                deliveryAdapter.setDeliveryList(filterDeliveries());
            }
            else if (sectionNumber == 2) {
                pickUpAdapter.setPickUpModelList(filterPickups());
            }
            recyclerView.scrollToPosition(0);
        }

        private void updateFilterStatus(String newFilterStatus) {
            filterType = newFilterStatus;
            if (sectionNumber == 1) {
                deliveryAdapter.setDeliveryList(filterDeliveries());
            }
            else if (sectionNumber == 2) {
                pickUpAdapter.setPickUpModelList(filterPickups());
            }
            recyclerView.scrollToPosition(0);
        }

        private List<DeliveryModel> filterDeliveries() {
            String lowerCaseQuery = searchQuery == null ? "" : searchQuery.toLowerCase();
            String lowerCaseFilterType = filterType == null ? "" : filterType.toLowerCase();
            List<DeliveryModel> filteredModelList = new ArrayList<>();
            List<DeliveryModel> models = RDApplication.getDeliveryModels();
            for (DeliveryModel model : models) {
                if (TextUtils.isEmpty(lowerCaseQuery) || model.getName().toLowerCase().contains(lowerCaseQuery)) {
                    if (TextUtils.isEmpty(lowerCaseFilterType) || lowerCaseFilterType.equalsIgnoreCase("all") || model.getMode().equalsIgnoreCase(lowerCaseFilterType)) {
                        filteredModelList.add(model);
                    }
                }
            }
            return filteredModelList;

        }

        private List<PickUpModel> filterPickups() {
            String lowerCaseQuery = searchQuery == null ? "" : searchQuery.toLowerCase();
            String lowerCaseFilterType = filterType == null ? "" : filterType.toLowerCase();
            List<PickUpModel> filteredModelList = new ArrayList<>();
            List<PickUpModel> models = RDApplication.getPickupModels();
            for (PickUpModel model : models) {
                if (TextUtils.isEmpty(lowerCaseQuery) || model.getName().toLowerCase().contains(lowerCaseQuery)) {
                    if (TextUtils.isEmpty(lowerCaseFilterType) || lowerCaseFilterType.equalsIgnoreCase("all") || model.getMode().equalsIgnoreCase(lowerCaseFilterType)) {
                        filteredModelList.add(model);
                    }
                }
            }
            return filteredModelList;

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
