package com.wang.catchcrazycat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.wang.android_lib.util.DialogUtil;
import com.wang.catchcrazycat.R;
import com.wang.catchcrazycat.game.LevelRule;
import com.wang.catchcrazycat.util.P;
import com.wang.catchcrazycat.util.Util;
import com.wang.catchcrazycat.view.Playground;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.play_ground)
    Playground playground;
    @Bind(R.id.tv_max_level)
    TextView tvMaxLevel;
    @Bind(R.id.tv_current_level)
    TextView tvCurrentLevel;
    @Bind(R.id.tv_step)
    TextView tvStep;
    @Bind(R.id.tv_current_level_description)
    TextView tvCurrentLevelDescription;

    private SlidingMenu slidingMenu;
    private BroadcastReceiver receiver;
    private TextView tvPlayerName;

    public static final String ACTION_STEP_CHANGED = "com.wang.action.step_changed";
    public static final String ACTION_MAX_LEVEL_CHANGED = "com.wang.action.max_level_changed";
    public static final String ACTION_CURRENT_LEVEL_CHANGED = "com.wang.action.current_level_changed";
    public static final String ACTION_SHOW_PLAYER_NAME = "com.wang.action.show_player_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBroadCast();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initSlidingMenu();
    }

    private void initBroadCast() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_STEP_CHANGED:
                        int step = intent.getIntExtra("step", 0);
                        tvStep.setText(String.valueOf(step));
                        break;
                    case ACTION_CURRENT_LEVEL_CHANGED:
                        int currentLevel = intent.getIntExtra("currentLevel", 0);
                        tvCurrentLevel.setText(LevelRule.getLevelString(currentLevel) +
                                "(" + currentLevel + ")");
                        tvCurrentLevelDescription.setText(LevelRule.getLevelDescription(currentLevel));
                        break;
                    case ACTION_MAX_LEVEL_CHANGED:
                        int maxLevel = intent.getIntExtra("maxLevel", 0);
                        tvMaxLevel.setText(LevelRule.getLevelString(maxLevel) +
                                "(" + maxLevel + ")");
                        break;
                    case ACTION_SHOW_PLAYER_NAME:
                        tvPlayerName.setText(P.getPlayerName());
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STEP_CHANGED);
        filter.addAction(ACTION_MAX_LEVEL_CHANGED);
        filter.addAction(ACTION_CURRENT_LEVEL_CHANGED);
        filter.addAction(ACTION_SHOW_PLAYER_NAME);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initSlidingMenu() {
        slidingMenu = Util.buildSlidingMenu(this, R.layout.sliding_menu);
        slidingMenu.setBackgroundResource(R.mipmap.bg_welcome);
        slidingMenu.findViewById(R.id.btn_menu_level_list).setOnClickListener(this);
        slidingMenu.findViewById(R.id.btn_menu_choose_level).setOnClickListener(this);
        tvPlayerName = (TextView) slidingMenu.findViewById(R.id.tv_player_name);
        tvPlayerName.setText(P.getPlayerName());
    }

    @OnClick({R.id.btn_menu, R.id.btn_play_new, R.id.btn_test, R.id.btn_previous_level, R.id.btn_next_level})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_menu_level_list://侧滑菜单的封神榜按钮
                break;
            case R.id.btn_menu_choose_level://侧滑菜单的选择等级按钮
                chooseLevel();
                slidingMenu.toggle();
                break;
            case R.id.btn_menu:
                slidingMenu.toggle();
                break;
            case R.id.btn_previous_level:
                playground.playPreviousLevel();
                break;
            case R.id.btn_next_level:
                playground.playNextLevel(false);
                break;
            case R.id.btn_play_new:
                playground.playNew();
                break;
            case R.id.btn_test:
                break;
        }
    }

    private void chooseLevel() {
        DialogUtil.showInputDialog(this, "选择等级", "输入等级数字1-13", "1", new DialogUtil.OnInputFinishListener() {
            @Override
            public void onInputFinish(String text) {
                try {
                    playground.setCurrentLevelAndPlay(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "不是数字", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}