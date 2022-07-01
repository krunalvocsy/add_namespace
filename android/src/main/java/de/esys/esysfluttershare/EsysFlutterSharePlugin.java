package de.esys.esysfluttershare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * EsysFlutterSharePlugin
 */
public class EsysFlutterSharePlugin implements FlutterPlugin, MethodCallHandler {

    private final String PROVIDER_AUTH_EXT = ".fileprovider.github.com/orgs/esysberlin/esys-flutter-share";

    private Context context;
    private MethodChannel methodChannel;

    public EsysFlutterSharePlugin() {}

    /**
     * Plugin registration.
     */
    @SuppressWarnings("deprecation")
    public static void registerWith(Registrar registrar) {

        final EsysFlutterSharePlugin instance = new EsysFlutterSharePlugin();
        instance.onAttachedToEngine(registrar.context(), registrar.messenger());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("text")) {
            text(call.arguments);
        }
        if (call.method.equals("file")) {
            file(call.arguments);
        }
        if (call.method.equals("files")) {
            files(call.arguments);
        }
    }

    private void text(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
        String title = argsMap.get("title");
        String text = argsMap.get("text");
        String mimeType = argsMap.get("mimeType");

        Intent shareIntent = new Intent();
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType(mimeType);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooserIntent = Intent.createChooser(shareIntent, title);
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooserIntent);

    }

    private void file(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
        String title = argsMap.get("title");
        String name = argsMap.get("name");
        String mimeType = argsMap.get("mimeType");
        String text = argsMap.get("text");

        Intent shareIntent = new Intent();
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.setType(mimeType);
        File file = new File(context.getCacheDir(), name);
        String fileProviderAuthority = context.getPackageName() + PROVIDER_AUTH_EXT;
        Uri contentUri = FileProvider.getUriForFile(context, fileProviderAuthority, file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        // add optional text
        if (!text.isEmpty()) shareIntent.putExtra(Intent.EXTRA_TEXT, text);


        Intent chooserIntent = Intent.createChooser(shareIntent, title);
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooserIntent);

    }

    private void files(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> argsMap = (HashMap<String, Object>) arguments;
        String title = (String) argsMap.get("title");

        @SuppressWarnings("unchecked")
        ArrayList<String> names = (ArrayList<String>) argsMap.get("names");
        String mimeType = (String) argsMap.get("mimeType");
        String text = (String) argsMap.get("text");

        Intent shareIntent = new Intent();
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

        shareIntent.setType(mimeType);

        ArrayList<Uri> contentUris = new ArrayList<>();

        for (String name : names) {
            File file = new File(context.getCacheDir(), name);
            String fileProviderAuthority = context.getPackageName() + PROVIDER_AUTH_EXT;
            contentUris.add(FileProvider.getUriForFile(context, fileProviderAuthority, file));
        }

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris);
        // add optional text
        if (!text.isEmpty()) shareIntent.putExtra(Intent.EXTRA_TEXT, text);


        Intent chooserIntent = Intent.createChooser(shareIntent, title);
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooserIntent);
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
    }

    private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
        this.context = applicationContext;
        methodChannel = new MethodChannel(messenger, "flutter_share");
        methodChannel.setMethodCallHandler(this);

        final MethodChannel channel = new MethodChannel(messenger, "channel:github.com/orgs/esysberlin/esys-flutter-share");
        channel.setMethodCallHandler(this);

    }
    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        context = null;
        methodChannel.setMethodCallHandler(null);
        methodChannel = null;
    }
}
