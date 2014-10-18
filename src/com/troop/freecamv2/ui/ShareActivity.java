package com.troop.freecamv2.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.troop.freecamv2.camera.modules.I_WorkEvent;
import com.troop.freecamv2.camera.modules.ModuleHandler;
import com.troop.freecamv2.camera.modules.PictureModule;

import java.io.File;

/**
 * Created by troop on 18.10.2014.
 */
public class ShareActivity extends MainActivity_v2 implements I_WorkEvent
{
    Intent callerIntent;
    Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.callerIntent = getIntent();
        data = callerIntent.getData();
        Uri imageUri = (Uri) callerIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);

        cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(this);
        if(cameraUiWrapper.moduleHandler.GetCurrentModuleName() != ModuleHandler.MODULE_PICTURE)
            cameraUiWrapper.moduleHandler.SetModule(ModuleHandler.MODULE_PICTURE);
        PictureModule pictureModule = (PictureModule)cameraUiWrapper.moduleHandler.GetCurrentModule();
        pictureModule.OverRidePath = imageUri.getPath();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void sendBackToCallerIntent()
    {

    }

    @Override
    public String WorkHasFinished(File filePath)
    {

        //shareIntent.setData(Uri.fromFile(filePath));
        //callerIntent.setAction(Intent.ACTION_SEND);

        callerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePath));
        //callerIntent.setType("image/jpeg");

        setResult(Activity.RESULT_OK, callerIntent);
        finish();
        return null;
    }
}
