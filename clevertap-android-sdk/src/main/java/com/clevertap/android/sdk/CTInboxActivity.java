package com.clevertap.android.sdk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * This activity shows the {@link CTInboxMessage} objects as per {@link CTInboxStyleConfig} style parameters
 */
public class CTInboxActivity extends FragmentActivity implements CTInboxListViewFragment.InboxListener {
    interface InboxActivityListener{
        void messageDidShow(CTInboxActivity ctInboxActivity, CTInboxMessage inboxMessage, Bundle data);
        void messageDidClick(CTInboxActivity ctInboxActivity, CTInboxMessage inboxMessage, Bundle data);
    }
    private CleverTapInstanceConfig config;
    private WeakReference<InboxActivityListener> listenerWeakReference;

    private String getFragmentTag() {
        return config.getAccountId() +":CT_INBOX_LIST_VIEW_FRAGMENT";
    }

    void setListener(InboxActivityListener listener) {
        listenerWeakReference = new WeakReference<>(listener);
    }

    InboxActivityListener getListener() {
        InboxActivityListener listener = null;
        try {
            listener = listenerWeakReference.get();
        } catch (Throwable t) {
            // no-op
        }
        if (listener == null) {
            config.getLogger().verbose(config.getAccountId(),"InboxActivityListener is null for notification inbox " );
        }
        return listener;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        CTInboxStyleConfig styleConfig;
        CleverTapAPI cleverTapAPI;
        try{
            Bundle extras = getIntent().getExtras();
            if(extras == null) throw new IllegalArgumentException();
            styleConfig = extras.getParcelable("styleConfig");
            config = extras.getParcelable("config");
            cleverTapAPI = CleverTapAPI.instanceWithConfig(getApplicationContext(), config);
            if (cleverTapAPI != null) {
                setListener(cleverTapAPI);
            }
        }catch (Throwable t){
            Logger.v("Cannot find a valid notification inbox bundle to show!", t);
            return;
        }

        setContentView(R.layout.inbox_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //noinspection ConstantConditions
        toolbar.setTitle(styleConfig.getNavBarTitle());
        toolbar.setTitleTextColor(Color.parseColor(styleConfig.getNavBarTitleColor()));
        toolbar.setBackgroundColor(Color.parseColor(styleConfig.getNavBarColor()));
        Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        drawable.setColorFilter(Color.parseColor(styleConfig.getBackButtonColor()),PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout linearLayout = findViewById(R.id.inbox_linear_layout);
        linearLayout.setBackgroundColor(Color.parseColor(styleConfig.getInboxBackgroundColor()));
        final TabLayout tabLayout = linearLayout.findViewById(R.id.tab_layout);
        final ViewPager viewPager = linearLayout.findViewById(R.id.view_pager);
        TextView noMessageView = findViewById(R.id.no_message_view);
        Bundle bundle = new Bundle();
        bundle.putParcelable("config", config);
        bundle.putParcelable("styleConfig", styleConfig);

        if (!styleConfig.isUsingTabs()) {
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            final FrameLayout listViewFragmentLayout = findViewById(R.id.list_view_fragment);
            listViewFragmentLayout.setVisibility(View.VISIBLE);
            if(cleverTapAPI!= null && cleverTapAPI.getInboxMessageCount() == 0){
                noMessageView.setBackgroundColor(Color.parseColor(styleConfig.getInboxBackgroundColor()));
                noMessageView.setVisibility(View.VISIBLE);
            }else {
                noMessageView.setVisibility(View.GONE);
                CTInboxListViewFragment listView = new CTInboxListViewFragment();
                listView.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.list_view_fragment, listView, getFragmentTag())
                        .commit();
            }
        } else {
            viewPager.setVisibility(View.VISIBLE);
            final CTInboxTabAdapter inboxTabAdapter = new CTInboxTabAdapter(getSupportFragmentManager());
            tabLayout.setVisibility(View.VISIBLE);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(styleConfig.getSelectedTabIndicatorColor()));
            tabLayout.setTabTextColors(Color.parseColor(styleConfig.getUnselectedTabColor()),Color.parseColor(styleConfig.getSelectedTabColor()));
            tabLayout.setBackgroundColor(Color.parseColor(styleConfig.getTabBackgroundColor()));
            tabLayout.addTab(tabLayout.newTab().setText("ALL"));

            Bundle _allBundle = (Bundle)bundle.clone();
            _allBundle.putInt("position",0);
            CTInboxListViewFragment all = new CTInboxListViewFragment();
            all.setArguments(_allBundle);
            inboxTabAdapter.addFragment(all,"ALL");

            ArrayList<String>tabs = styleConfig.getTabs();

            for (int i=0; i<tabs.size(); i++) {
                String filter = tabs.get(i);
                int pos = i + 1;
                Bundle _bundle = (Bundle)bundle.clone();
                _bundle.putInt("position", pos);
                _bundle.putString("filter", filter);
                CTInboxListViewFragment frag = new CTInboxListViewFragment();
                frag.setArguments(_bundle);
                inboxTabAdapter.addFragment(frag, filter);
                viewPager.setOffscreenPageLimit(pos);
            }

            viewPager.setAdapter(inboxTabAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    CTInboxListViewFragment fragment = (CTInboxListViewFragment) inboxTabAdapter.getItem(tab.getPosition());
                    if(fragment != null && fragment.mediaRecyclerView !=null){
                        fragment.mediaRecyclerView.onRestartPlayer();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    CTInboxListViewFragment fragment = (CTInboxListViewFragment) inboxTabAdapter.getItem(tab.getPosition());
                    if(fragment != null && fragment.mediaRecyclerView != null){
                        fragment.mediaRecyclerView.onPausePlayer();
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    //no-op
                }
            });
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    public void messageDidShow(Context baseContext, CTInboxMessage inboxMessage, Bundle data) {
        didShow(data,inboxMessage);
    }

    @Override
    public void messageDidClick(Context baseContext, CTInboxMessage inboxMessage, Bundle data) {
        didClick(data,inboxMessage);
    }

    void didClick(Bundle data, CTInboxMessage inboxMessage) {
        InboxActivityListener listener = getListener();
        if (listener != null) {
            listener.messageDidClick(this,inboxMessage, data);
        }
    }

    void didShow(Bundle data, CTInboxMessage inboxMessage) {
        InboxActivityListener listener = getListener();
        if (listener != null) {
            listener.messageDidShow(this,inboxMessage, data);
        }
    }
}
