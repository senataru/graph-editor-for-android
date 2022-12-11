package com.example.graph_editor.extensions;

import android.os.Bundle;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.client.Client;
import com.example.graph_editor.model.extensions.ExtensionsClient;
import com.example.graph_editor.model.extensions.ExtensionsRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ExtensionsActivity extends AppCompatActivity {
    RecyclerView installedView;
    RecyclerView availableView;

    //TODO remove and inject it instead
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
                InstalledExtensionsProvider.getInstance(this.getFilesDir());

        //TODO change to async
        CompletableFuture<Pair<ExtensionsClient, List<String>>> future =
                CompletableFuture.supplyAsync(() -> {
            ExtensionsClient c = new Client("192.168.43.113");
            try {
                c.connect();
                return Pair.create(c, c.getExtensionsList());
            } catch (Exception e) {
               return Pair.create(
                       new ExtensionsClient() {
                           @Override
                           public List<String> getExtensionsList() {
                               return new ArrayList<>();
                           }
                           @Override
                           public void downloadExtension(File root, String name) {}
                           @Override
                           public void connect() {}
                           @Override
                           public void disconnect() {}
                       },
                       new ArrayList<>());
            }
        });
        Pair<ExtensionsClient, List<String>> pair;
        try {
            pair = future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ExtensionsClient client = pair.first;
        List<String> availableExtensions = pair.second;

        installedView.setAdapter(new InstalledExtensionsRecyclerViewAdapter(
                this,
                installedRepository.getExtensions()
                ));
        availableView.setAdapter(new AvailableExtensionsRecyclerViewAdapter(
                this,
                availableExtensions,
                installedRepository,
                client
        ));
    }
}
