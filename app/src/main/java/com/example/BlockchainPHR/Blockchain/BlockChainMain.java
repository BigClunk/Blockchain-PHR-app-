package com.example.BlockchainPHR.Blockchain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.BlockchainPHR.HomeActivity;
import com.example.BlockchainPHR.R;
import com.example.BlockchainPHR.databinding.ActivityMainBinding;
import com.example.BlockchainPHR.databinding.ContentMainBinding;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

public class BlockChainMain extends AppCompatActivity implements View.OnClickListener {

    private ContentMainBinding viewBindingContent;

    private ProgressDialog progressDialog;
    private BlockChainManager blockChain;
    private SharedPreferencesManager prefs;
    private boolean isEncryptionActivated, isDarkThemeActivated;
    private AppUpdateManager appUpdateManager;

    private static final int UPDATE_REQUEST_CODE = 1000;
    private static final String TAG_POW_DIALOG = "proof_of_work_dialog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        prefs = new SharedPreferencesManager(this);

        super.onCreate(savedInstanceState);
        ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        viewBindingContent = ContentMainBinding.bind(viewBinding.contentMain.getRoot());
        setContentView(viewBinding.getRoot());
        setSupportActionBar(viewBinding.toolbar);

        // Check a possible update from Play Store
        checkUpdate();

        isEncryptionActivated = prefs.getEncryptionStatus();

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        viewBindingContent.recyclerContent.setHasFixedSize(true);
        // Use a linear layout manager
        viewBindingContent.recyclerContent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // Setting the Progress Dialog
        showProgressDialog(getResources().getString(R.string.text_creating_block_chain));

        // Starting BlockChain request on a thread
        new Thread(() -> runOnUiThread(() -> {
            // Initializing BlockChain...
            // the CPU will has to find a hash for the block
            // starting with a given number of zeros.
            blockChain = new BlockChainManager(this, prefs.getPowValue());
            viewBindingContent.recyclerContent.setAdapter(blockChain.adapter);
            cancelProgressDialog(progressDialog);
        })).start();

        viewBindingContent.btnSendData.setOnClickListener(this);
    }

    // Check a possible update from Play Store
    private void checkUpdate() {
        // Creates instance of the manager
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update
                startTheUpdate(appUpdateManager, appUpdateInfo);
            }
        });

    }

    // If an update exist, request for the update
    private void startTheUpdate(@NonNull AppUpdateManager appUpdateManager, @NonNull AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request
                    this,
                    // Include a request code to later monitor this update request
                    UPDATE_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Continue with the update if one exists
        resumeTheUpdate();
    }

    // Continue with the update if one exists
    private void resumeTheUpdate() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                startTheUpdate(appUpdateManager, appUpdateInfo);
                            }

                        });
    }

    // Starting new request on a thread
    private void startBlockChain() {
        // Setting the Progress Dialog
        showProgressDialog(getResources().getString(R.string.text_mining_blocks));

        runOnUiThread(() -> {
            if (blockChain != null && viewBindingContent.editMessage.getText() != null && viewBindingContent.recyclerContent.getAdapter() != null) {
                String message = viewBindingContent.editMessage.getText().toString();

                if (!message.isEmpty()) {

                    // Verification if encryption is activated
                    if (!isEncryptionActivated) {
                        // Broadcast data
                        blockChain.addBlock(blockChain.newBlock(message));
                    } else {
                        try {
                            // Broadcast data
                            blockChain.addBlock(blockChain.newBlock(CipherUtils.encryptIt(message).trim()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, R.string.error_something_wrong, Toast.LENGTH_LONG).show();
                        }
                    }
                    viewBindingContent.recyclerContent.scrollToPosition(blockChain.adapter.getItemCount() - 1);

                    // Validate block's data
                    System.out.println(getResources().getString(R.string.log_block_chain_valid, blockChain.isBlockChainValid()));
                    if (blockChain.isBlockChainValid()) {
                        // Preparing data to insert to RecyclerView
                        viewBindingContent.recyclerContent.getAdapter().notifyDataSetChanged();
                        // Cleaning the EditText
                        viewBindingContent.editMessage.setText("");
                    } else {
                        Toast.makeText(this, R.string.error_block_chain_corrupted, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, R.string.error_empty_data, Toast.LENGTH_LONG).show();
                }

                cancelProgressDialog(progressDialog);
            } else {
                Toast.makeText(this, R.string.error_something_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.btn_send_data) {
            // Start new request on a UI thread
            startBlockChain();
        }
    }

    // Setting the Progress Dialog
    private void showProgressDialog(@NonNull String loadingMessage) {
        progressDialog = new ProgressDialog(BlockChainMain.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    private void cancelProgressDialog(@Nullable ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_pow:
                PowFragment powFragment = PowFragment.newInstance();
                powFragment.show(this.getSupportFragmentManager(), TAG_POW_DIALOG);
                break;

            case R.id.action_encrypt:
                isEncryptionActivated = !item.isChecked();
                item.setChecked(isEncryptionActivated);
                if (item.isChecked()) {
                    Toast.makeText(this, R.string.text_encryption_activated, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.text_encryption_deactivated, Toast.LENGTH_SHORT).show();
                }
                prefs.setEncryptionStatus(isEncryptionActivated);
                return true;


            case R.id.action_exit:
                startActivity(new Intent(BlockChainMain.this, HomeActivity.class));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

}