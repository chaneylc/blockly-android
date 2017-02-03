/*
 *  Copyright 2017 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.google.blockly.android;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.blockly.android.control.BlocklyController;
import com.google.blockly.android.ui.DeleteVariableDialog;
import com.google.blockly.android.ui.NameVariableDialog;
import com.google.blockly.model.Block;

import java.util.List;

/**
 * Default implementation of {@link BlocklyController.VariableCallback}. It uses
 * {@link DeleteVariableDialog} and {@link NameVariableDialog} by default, but either can be
 * replaced by overriding {@link #newDeleteVariableDialog()} or {@link #newNameVariableDialog()},
 * respectively.
 */
public class DefaultVariableCallback extends BlocklyController.VariableCallback {
    private AppCompatActivity mActivity;
    private BlocklyController mController;

    private final NameVariableDialog.Callback mRenameCallback = new NameVariableDialog
            .Callback() {
        @Override
        public void onNameConfirmed(String oldName, String newName) {
            mController.renameVariable(oldName, newName);
        }
    };
    private final NameVariableDialog.Callback mCreateCallback = new NameVariableDialog
            .Callback() {
        @Override
        public void onNameConfirmed(String originalName, String newName) {
            mController.addVariable(newName);
        }
    };

    public DefaultVariableCallback(AppCompatActivity activity, BlocklyController controller) {
        mActivity = activity;
        mController = controller;
    }

    @Override
    public boolean onDeleteVariable(String variable) {
        if (!mController.isVariableInUse(variable)) {
            return true;
        }
        List<Block> blocks = mController.getBlocksWithVariable(variable);
        if (blocks.size() == 1) {
            // For one block just let the controller delete it.
            return true;
        }

        DeleteVariableDialog deleteVariableDialog = newDeleteVariableDialog();
        deleteVariableDialog.setController(mController);
        deleteVariableDialog.setVariable(variable, blocks.size());
        deleteVariableDialog.show(mActivity.getSupportFragmentManager(), "DeleteVariable");
        return false;
    }

    @Override
    public boolean onRenameVariable(String variable, String newName) {
        NameVariableDialog nameVariableDialog = newNameVariableDialog();
        nameVariableDialog.setVariable(variable, mRenameCallback, true);
        nameVariableDialog.show(mActivity.getSupportFragmentManager(), "RenameVariable");
        return false;
    }

    @Override
    public boolean onCreateVariable(String variable) {
        NameVariableDialog nameVariableDialog = newNameVariableDialog();
        nameVariableDialog.setVariable(variable, mCreateCallback, false);
        nameVariableDialog.show(mActivity.getSupportFragmentManager(), "CreateVariable");
        return false;
    }

    @NonNull
    protected DeleteVariableDialog newDeleteVariableDialog() {
        return new DeleteVariableDialog();
    }

    @NonNull
    protected NameVariableDialog newNameVariableDialog() {
        return new NameVariableDialog();
    }
}