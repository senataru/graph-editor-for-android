package com.example.graph_editor.extensions;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.client.Client;
import com.example.graph_editor.model.extensions.ExtensionsClient;
import com.example.graph_editor.model.extensions.ExtensionsRepository;
import com.example.graph_editor.model.graph_storage.GraphWriter;

import java.time.Duration;
import java.util.List;

interface OnExtensionInstallClicked {
    void onInstallClicked(String extensionName);
}

public class ExtensionsActivity extends AppCompatActivity implements OnExtensionInstallClicked {
    RecyclerView installedView;
    RecyclerView availableView;
    EditText ipTextEdit;
    ExtensionsClient client;
    String ip;

    //TODO remove static and inject it instead
    private static ExtensionsRepository installedRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) { ip = savedInstanceState.getString(serverIpKey); };
        setContentView(R.layout.activity_extensions);
        installedView = findViewById(R.id.installedExtensions);
        availableView = findViewById(R.id.availableExtensions);
        ipTextEdit = findViewById(R.id.serverAddress);
        ipTextEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                ip = s.toString();
                tryConnectAsync();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(installedRepository == null) installedRepository =
                InstalledExtensionsProvider.getInstance(this.getFilesDir());
        ip = ipTextEdit.getText().toString();
        tryConnectAsync();

        installedView.setAdapter(new InstalledExtensionsRecyclerViewAdapter(
                this,
                installedRepository.getExtensions()
        ));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(serverIpKey, ip);
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
    private void tryConnectAsync() {
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
