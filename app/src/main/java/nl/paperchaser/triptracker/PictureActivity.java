package nl.paperchaser.triptracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureActivity extends MainActivity{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageView;
    
    private void dispatchTakePictureIntent(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = findViewById(R.id.imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmmap);
        }


    }
    String currentPhotoPath;

    private File createImageFile() throws IOException{
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /*prefix*/
                ".jpg", /*suffix*/
                storageDir    /*directory*/
        );
        //Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent_1(){
        Intent takePictureIntent_1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camara activity to handle the intent
        if (takePictureIntent_1.resolveActivity(getPackageManager()) != null) {
            //Create the file where the photo should go
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException ex){
                // Error occured while creating the File
                ex.printStackTrace();
            }
            //Continue only if the File was succefully created
            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "nl.paperchaser.triptracker",
                        photoFile);
                takePictureIntent_1.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent_1,REQUEST_TAKE_PHOTO);
            }

        }
    }

    private void gallerryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        //Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        //Determine how much scale down the image
        int scaleFactor = Math.min(photoW, photoH/targetH);

        //Decode the image file into a bitmap sizedf to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }




}
