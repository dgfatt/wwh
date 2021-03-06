/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.wwh.androidarchitecture.google_sample_todo_mvp;

import android.support.v7.app.AppCompatActivity;

/**
 * Displays task details screen.
 * 初始化了fragment的activity
 * activity在项目中是一个全局的控制者，负责创建view以及presenter实例，并将二者联系起来
 */
public class TaskDetailActivity extends AppCompatActivity {
//
//    public static final String EXTRA_TASK_ID = "TASK_ID";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.taskdetail_act);
//
//        // Set up the toolbar.
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setDisplayShowHomeEnabled(true);
//
//        // Get the requested task id
//        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
//
//        TaskDetailFragment taskDetailFragment = (TaskDetailFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.contentFrame);
//
//        // 初始化fragment
//        if (taskDetailFragment == null) {
//            taskDetailFragment = TaskDetailFragment.newInstance(taskId);
//
//            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
//                    taskDetailFragment, R.id.contentFrame);
//        }
//
//        // Create the presenter
//        // 创建presenter
//        new TaskDetailPresenter(
//                taskId,
//                Injection.provideTasksRepository(getApplicationContext()),
//                taskDetailFragment);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
}
