package com.zmt.e_read.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.zmt.e_read.Adapter.ChannelsAdapter;
import com.zmt.e_read.Animator.FABAnimator;
import com.zmt.e_read.R;
import com.zmt.e_read.Module.ChannelData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    private View view;
    private List<ChannelData> channelDataList;
    @BindView(R.id.channelTab) TabLayout channelTab;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.addChannel) ImageView addChannel;
    @BindView(R.id.fab) FloatingActionButton fab;
    public static final String FILTER = "com.zmt.e_read.broadCast.adjustNewsFab";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_news, container, false);
        initViews();

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FILTER);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Animation animation;
                if (intent.getStringExtra("direction").equals("up")){
                    if(fab.getVisibility() == View.VISIBLE){
//                        FABAnimator.hideFAB(fab, fab.getHeight());
                        animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_out);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                fab.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        fab.startAnimation(animation);
                    }
                } else {
                    if(fab.getVisibility() == View.GONE){
//                        FABAnimator.showFAB(fab, fab.getHeight());
                        animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_in);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                fab.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        fab.startAnimation(animation);
                    }
                }
            }
        };
        manager.registerReceiver(receiver, intentFilter);
        return view;
    }

    public void initViews(){
        ButterKnife.bind(this, view);
        List<Fragment> newsFragmentList = new ArrayList<>();

        /**
         * 设置TabLayout模式
         */
        channelTab.setTabMode(TabLayout.MODE_FIXED);

        /**
         * 初始化channelData对象
         */
        String [] channelName = getResources().getStringArray(R.array.channelName);
        String [] channelID = getResources().getStringArray(R.array.channelID);
        channelDataList = new ArrayList<>();
        for (int i = 0; i < channelID.length; i++) {
            ChannelData channelData = new ChannelData();
            channelData.setId(channelID[i]).setName(channelName[i]);
            switch (channelData.getName()){
                case ChannelData.HEADLINE :
                    channelData.setType(ChannelData.HEADLINE_TYPE);
                    break;
                case ChannelData.HOUSE :
                    channelData.setType(ChannelData.HOUSE_TYPE);
                    break;
                default:
                    channelData.setType(ChannelData.OTHER_TYPE);
                    break;
            }
            channelDataList.add(channelData);
        }

        /**
         * 为TabLayout添加tab名称
         */
        for (int i = 0; i < channelID.length; i++) {
            NewsListFragment newsListFragment = new NewsListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ChannelData.channelID, channelDataList.get(i).getId());
            bundle.putString(ChannelData.channelName, channelDataList.get(i).getName());
            bundle.putString(ChannelData.channelType, channelDataList.get(i).getType());
            newsListFragment.setArguments(bundle);
            newsFragmentList.add(newsListFragment);
            channelTab.addTab(channelTab.newTab().setText(channelDataList.get(i).getName()));
        }

        /**
         * 加载viewPager的adapter
         */
        ChannelsAdapter adapter = new ChannelsAdapter(getChildFragmentManager(),
                Arrays.asList(channelName), newsFragmentList);
        /**
         * 为TabLayout设置viewPager
         */
        viewPager.setAdapter(adapter);
        channelTab.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(NewsListFragment.FILTER);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        });
    }

}
