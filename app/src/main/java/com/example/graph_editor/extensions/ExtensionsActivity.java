package com.example.graph_editor.extensions;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.fs.FSDirectories;

import java.io.File;
import java.util.List;

import graph_editor.extensions.ExtensionsClient;
import graph_editor.extensions.ExtensionsRepository;
import graph_editor.extensions.client.Client;

interface OnExtensionInstallClicked {
    void onInstallClicked(String extensionName);
}

public class ExtensionsActivity extends AppCompatActivity implements OnExtensionInstallClicked {
    RecyclerView installedView;
    RecyclerView availableView;

    ExtensionsClient client;

    //TODO remove static and inject it instead
    private static ExtensionsRepository installedRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extensions);
        installedView = findViewById(R.id.installedExtensions);
        availableView = findViewById(R.id.availableExtensions);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(installedRepository == null) installedRepository =
                InstalledExtensionsProvider.getInstance(new File(this.getFilesDir(), FSDirectories.pluginsDirectory));
        new Thread(() -> tryConnect("192.168.43.113")).start();

        installedView.setAdapter(new InstalledExtensionsRecyclerViewAdapter(
                this,
                installedRepository.getExtensions()
        ));
    }

    // TODO move to presenter
    @Override
    public void onInstallClicked(String extensionName) {
        new Thread(() -> {
            try {
                client.downloadExtension(getFilesDir(), extensionName);
                installedRepository.add(extensionName);
                installedView.post(() -> installedView.getAdapter().notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void tryConnect(String serverAddress) {
        try {
            ExtensionsClient c = new Client(serverAddress);
            c.connect();
            List<String> availableExtensions = c.getExtensionsList();
            availableView.post(() -> availableView.setAdapter(new AvailableExtensionsRecyclerViewAdapter(
                            this,
                            availableExtensions,
                            installedRepository,
                            this
                            ))
            );
        } catch (Exception e) {
            availableView.post(() -> Toast
                    .makeText(
                        this,
                        "connection to server " + serverAddress  + " failed",
                        Toast.LENGTH_LONG)
                    .show()
            );
        }
    }
}
