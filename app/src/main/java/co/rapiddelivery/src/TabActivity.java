package co.rapiddelivery.src;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import co.rapiddelivery.RDApplication;
import co.rapiddelivery.adapters.DeliveryAdapter;
import co.rapiddelivery.adapters.PickUpAdapter;
import co.rapiddelivery.models.DeliveryModel;
import co.rapiddelivery.models.PickUpModel;
import co.rapiddelivery.network.APIClient;
import co.rapiddelivery.network.DRList;
import co.rapiddelivery.network.LoginResponse;
import co.rapiddelivery.network.ServerResponseBase;
import co.rapiddelivery.utils.KeyConstants;
import co.rapiddelivery.utils.SPrefUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabActivity extends AppCompatActivity {

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
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        getDRListFromServer();
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
        ((RDApplication)getApplication()).setAppOwnerData(null);
        ((RDApplication)getApplication()).setDeliverySetModel(null);
        ((RDApplication)getApplication()).setPickupSetModel(null);
        Intent intent = new Intent(mActivityContext, LoginActivity.class);
        mActivityContext.startActivity(intent);
        finish();
    }

    public void getDRListFromServer() {

        String loginDetails = SPrefUtils.getStringPreference(this, SPrefUtils.LOGGEDIN_USER_DETAILS);
        LoginResponse loginResponse = new Gson().fromJson(loginDetails, LoginResponse.class);

        /*APIClient.getClient().getDRList(loginResponse.getName(), loginResponse.getPassword(), loginResponse.getEmp_id())
                .enqueue(new Callback<DRList>() {
                    @Override
                    public void onResponse(Call<DRList> call, Response<DRList> response) {
                        DRList drList = response.body();
                    }

                    @Override
                    public void onFailure(Call<DRList> call, Throwable t) {

                    }
                });*/
        Log.e("list", loginResponse.getUserName()+ " " +loginResponse.getPassword()+ " " + loginResponse.getEmp_id());
        APIClient.getClient().getList(loginResponse.getUserName(), loginResponse.getPassword(), loginResponse.getEmp_id())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("list", response.code() + "  " + response.toString());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

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

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            if (sectionNumber == 1) {
                List<DeliveryModel> deliveryModels = RDApplication.getDeliverySetModel().getDeliveryModels();
                DeliveryAdapter adapter = new DeliveryAdapter(this.getContext(), deliveryModels, new DeliveryAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(DeliveryModel deliveryModel) {
                        Intent intent = new Intent(getActivity(), DeliveryDetailsActivity.class);
                        intent.putExtra(KeyConstants.INTENT_EXTRA_DELIVERY_NUMBER, deliveryModel.getTrackingNumber());
                        getActivity().startActivity(intent);
                    }
                });

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
            else if (sectionNumber == 2) {
                List<PickUpModel> deliveryModels = RDApplication.getPickupSetModel().getPickupSetModels();
                PickUpAdapter adapter = new PickUpAdapter(this.getContext(), deliveryModels, new PickUpAdapter.OnItemClickListener() {
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
                recyclerView.setAdapter(adapter);
            }

            return rootView;
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
