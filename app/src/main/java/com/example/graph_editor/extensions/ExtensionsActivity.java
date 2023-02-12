package com.example.graph_editor.extensions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    void onInstallClicked(String extensionName, int position);
}

public class ExtensionsActivity extends AppCompatActivity implements OnExtensionInstallClicked {
    RecyclerView installedView;
    RecyclerView availableView;
    EditText ipTextEdit;
    ExtensionsClient client;
    Button connectButton;

    //TODO remove static and inject it instead
    private static ExtensionsRepository installedRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getSharedPreferences("config", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extensions);
        installedView = findViewById(R.id.installedExtensions);
        availableView = findViewById(R.id.availableExtensions);
        ipTextEdit = findViewById(R.id.serverIp);
        connectButton = findViewById(R.id.serverConnectButton);
        connectButton.setOnClickListener(v -> {
            String ip = ipTextEdit.getText().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(serverIpKey, ip);
            editor.apply();
            tryConnectAsync(ip);
        });
        String ip = sharedPref.getString(serverIpKey, getString(R.string.defaultServerIp));
        ipTextEdit.setText(ip);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(installedRepository == null) installedRepository =
                InstalledExtensionsProvider
                        .getInstance(
                                new PluginsProxyImpl(
                                        GraphMenuManagerImpl.getInstance(),
                                        new CanvasManagerImpl(),
                                        new GraphActionManagerImpl()),
                                new File(this.getFilesDir(), FSDirectories.pluginsDirectory)
                        );

        installedView.setAdapter(new InstalledExtensionsRecyclerViewAdapter(
                this,
                installedRepository.getExtensions()
        ));
    }

    // TODO move to presenter
    @Override
    public void onInstallClicked(String extensionName, int position) {
        new Thread(() -> {
            try {
                client.downloadExtension(new File(getFilesDir(), FSDirectories.pluginsDirectory), extensionName);
                installedRepository.add(extensionName);
                installedView.post(() -> installedView.getAdapter().notifyItemChanged(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void tryConnectAsync(String ip) {
        new Thread(() -> {
            try {
                client = new Client(ip);
                client.connect();
                List<String> availableExtensions = client.getExtensionsList();
                availableView.post(() -> availableView.setAdapter(new AvailableExtensionsRecyclerViewAdapter(
                                this,
                                availableExtensions,
                                installedRepository,
                                this
                        ))
                );
            } catch (Exception e) {
                availableView.post(() -> {
                            Toast.makeText(this, "connection to server " + ip  + " failed", Toast.LENGTH_LONG).show();
                            Toast.makeText(this, e.getClass().toString(), Toast.LENGTH_LONG).show();
                        }
                );
            }
        }).start();
    }
    private static String serverIpKey = "serverIp";
}
