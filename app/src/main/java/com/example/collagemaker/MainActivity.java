package com.example.collagemaker;

/*
    @author: Oliver Lester
    @description: This program is the logic side of the CollageMaker app. It has
        the code to set up the UI from the activity_main.xml file. It has two implicit
        intents. One which uses the phones camera to take a picture, and replace an
        image in an ImageView with the take picture. The other allows the user to
        send the collage of images to someone.
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static String currentPhotoPath;
    int curImageID;

    /**
     * This function takes in a bundle and creates the UI.
     * This function takes in one Bundle object as a parameter.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curImageID = 0;
        currentPhotoPath = "image.jpg";
        setContentView(R.layout.activity_main);
    }

    /**
     * This function is responsible for handling the onclick of the ImageViews. It uses
     *      and implicit intent to bring up the camera. Also creating a file for the new picture
     *      to be held at.
     * This function takes in one View object as a parameter.
     */
    public void imageClick(View view) {
        curImageID = view.getId();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), currentPhotoPath);
        Uri curUri = FileProvider.getUriForFile(this,
                "com.example.android.fileprovider", file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, curUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * This function handles what happens after the request of the camera is done.
     * This function takes in two ints that hold the request and result codes of the camera intent.
     *      And an Intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView imageView = findViewById(curImageID);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    currentPhotoPath);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

            //Bitmap.createBitmap(bitmap);
            imageView.setImageBitmap(Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), 800));
        }
    }

    /**
     * This function is responsible for handling the onclick of the share button. It takes a
     *      screenshot of the four ImageViews and creates a file for the bitmap. It then brings up
     *      the share feature of android, using an implicit intent.
     * This function takes in one View object as a parameter.
     */
    public void shareClick (View view) {
        View mView = findViewById(R.id.main);
        Bitmap bitmap = Bitmap.createBitmap(
                 mView.getWidth(), mView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mView.draw(canvas);

        String temp = "image.png";

        File file = null;
        try {
            file = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), temp);
            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(this,
                "com.example.android.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}