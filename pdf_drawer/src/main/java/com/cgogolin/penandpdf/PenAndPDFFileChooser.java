package com.cgogolin.penandpdf;

import java.io.File;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;

import com.google.android.material.tabs.TabLayout;

public class PenAndPDFFileChooser extends AppCompatActivity implements RecentFilesFragment.goToDirInterface {

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ViewPager mViewPager;

    private String mFilename = null;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
                //Set default preferences on first start
        PreferenceManager.setDefaultValues(this, SettingsActivity.SHARED_PREFERENCES_STRING, MODE_MULTI_PROCESS, R.xml.preferences, false);

            //Infalte the layout
        setContentView(R.layout.chooser);

            //Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            //Setup the tabs
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.browse));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.recent));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.notes));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            //Setup the view pager
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

                FileBrowserFragment fileBrowserFragment;
                RecentFilesFragment recentFilesFragment;
                NoteBrowserFragment noteBrowserFragment;
                
                @Override
                public int getCount() {
                    return 3;
                }        
                @Override
                public Fragment getItem(int position) {
                    switch(position) {
                        case 0:
                            if(fileBrowserFragment == null)
                                fileBrowserFragment = FileBrowserFragment.newInstance(getIntent());
                            return fileBrowserFragment;
                        case 1:
                            if(recentFilesFragment == null)
                                recentFilesFragment = RecentFilesFragment.newInstance(getIntent());
                            return recentFilesFragment;
                        case 2:
                            if(noteBrowserFragment == null)
                                noteBrowserFragment = NoteBrowserFragment.newInstance(getIntent());
                            return noteBrowserFragment;
                        default:
                            return null;
                    }
                }
            };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout)
            {
                @Override
                public void onPageSelected(int position) {
                        //When swiping between pages, select the corresponding tab.
                    tabLayout.getTabAt(position).select();
                        //...and give the fragments a chance to react to them becoming visible
                    FragmentPagerAdapter fragmentPagerAdapter = (FragmentPagerAdapter)mViewPager.getAdapter();
                    Fragment fragment = fragmentPagerAdapter.getItem(position);
                    
                    
                    switch(position)
                    {   
                        case 0:
                            ((FileBrowserFragment)fragment).inForground();
                            break;
                        case 1:
                            ((RecentFilesFragment)fragment).inForground();
                            break;
                        case 2:
                            ((NoteBrowserFragment)fragment).inForground();
                            break;
                    }
                }
            }                              
            );
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
 
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
 
            }
 
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
 
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//Inflates the options menu
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.empty_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Handel clicks in the options menu
        switch (item.getItemId()) 
        {
            case R.id.menu_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void goToDir(File dir) {
        ((FileBrowserFragment)mFragmentPagerAdapter.getItem(0)).goToDir(dir);       
        mViewPager.setCurrentItem(0);        
    }
    

    @Override
    public void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.animator.fade_in, R.animator.exit_to_left);
    }
}
