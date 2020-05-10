package com.michaelzap94.santabiblia;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.adapters.MainCardViewPagerAdapter;
import com.michaelzap94.santabiblia.models.MainCardContent;
import com.michaelzap94.santabiblia.utilities.CommonMethods;
import com.michaelzap94.santabiblia.utilities.ShadowTransformer;

import java.util.List;

public class MainActivity extends BaseActivityTopDrawer  {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private BottomNavigationView bottomNavigationView;
//    Integer[] colors = null;
    //    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    ViewPager viewPager;
    MainCardViewPagerAdapter mainCardViewPagerAdapter;
    List<MainCardContent> mainCardContents;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main_base);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        CommonMethods.checkDatabaseExistLoad(MainActivity.this);

        //===================================================================================
        viewPager = findViewById(R.id.main_card_mem_viewpager);

        mainCardViewPagerAdapter = new MainCardViewPagerAdapter(null, this);

//        models = new ArrayList<>();
//        models.add(new Model(R.drawable.ic_bookmark, "Brochure", "Brochure is an informative paper document (often also used for advertising) that can be folded into a template"));
//        models.add(new Model(R.drawable.ic_compare_arrows, "Sticker", "Sticker is a type of label: a piece of printed paper, plastic, vinyl, or other material with pressure sensitive adhesive on one side"));
//        models.add(new Model(R.drawable.ic_copy, "Poster", "Poster is any piece of printed paper designed to be attached to a wall or vertical surface."));
//        models.add(new Model(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
//        models.add(new Model(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
//        models.add(new Model(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
//        models.add(new Model(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
//        models.add(new Model(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
//        models.add(new Model(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
//        models.add(new Model(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
//        models = new ArrayList<>();
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_bookmark, "Brochure", "Brochure is an informative paper document (often also used for advertising) that can be folded into a template"));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_compare_arrows, "Sticker", "Sticker is a type of label: a piece of printed paper, plastic, vinyl, or other material with pressure sensitive adhesive on one side"));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_copy, "Poster", "Poster is any piece of printed paper designed to be attached to a wall or vertical surface."));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));
        mainCardViewPagerAdapter.addCardItem(new MainCardContent(R.drawable.ic_check_circle, "Namecard", "Business cards are cards bearing business information about a company or individual."));


        mCardShadowTransformer = new ShadowTransformer(viewPager, mainCardViewPagerAdapter);

        viewPager.setPadding(68, 8, 68, 10);
//        viewPager.setPageMargin(5);
        viewPager.setAdapter(mainCardViewPagerAdapter);
        viewPager.setPageTransformer(false, mCardShadowTransformer);
        viewPager.setOffscreenPageLimit(1);


//        Integer[] colors_temp = {
//                getResources().getColor(R.color.color1),
//                getResources().getColor(R.color.color2),
//                getResources().getColor(R.color.color3),
//                getResources().getColor(R.color.color4)
//        };
//
//        colors = colors_temp;

//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                if (position < (adapter.getCount() -1) && position < (colors.length - 1)) {
//                    viewPager.setBackgroundColor(
//
//                            (Integer) argbEvaluator.evaluate(
//                                    positionOffset,
//                                    colors[position],
//                                    colors[position + 1]
//                            )
//                    );
//                }
//
//                else {
//                    viewPager.setBackgroundColor(colors[colors.length - 1]);
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        //===================================================================================

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_home, MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_home);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }
}
