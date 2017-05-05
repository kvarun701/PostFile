package com.abeer.postfile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.github.lzyzsd.circleprogress.DonutProgress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
 import android.widget.AdapterView.OnItemSelectedListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;


/**
 * Created by varun on 5/3/2017.
 */
public class ImageUpload extends AppCompatActivity implements OnItemSelectedListener, View.OnClickListener {
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 23;
    private int PICK_PDF_REQUEST = 1;
    private  ImageView imageview;
    private ProgressDialog mProgressDialog;
    private  Button select_button;
    private   TextView tv;
    private int FLAG = 0;
    private Bitmap imageBitmap;
    private String imagePath="";
    private Intent intent;
    File sourceFile;
     static String item;
    private int mSelectedContentTypeIndex = 0;
    long totalSize = 0;
    String FILE_UPLOAD_URL = "http://192.168.0.192:8081/Eduber/webservices/contentUpload";
    LinearLayout uploader_area;
    LinearLayout progress_area;
    public DonutProgress donut_progress;
    private Spinner  mSpinnerContentType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.imageupload);
        initlayout();
    }


    private void initlayout() {
        uploader_area = (LinearLayout) findViewById(R.id.upload_area);
        progress_area = (LinearLayout) findViewById(R.id.progress_area);
         select_button = (Button) findViewById(R.id.button_select);
        Button upload_button = (Button) findViewById(R.id.button_upload);
        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);
         imageview = (ImageView) findViewById(R.id.img);
         tv = (TextView)findViewById(R.id.Info);
        mSpinnerContentType = (Spinner) findViewById(R.id.spinner);
        select_button.setOnClickListener(this) ;
        upload_button.setOnClickListener(this);
        mSpinnerContentType.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Audio");
        categories.add("Video");
        categories.add("Pdf");
        categories.add("images");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerContentType.setAdapter(dataAdapter);


        }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_select:
                selectFile();
                break;
            case R.id.button_upload:
                if (imagePath != null) {
                    new uploadFile().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "upload the file", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

   private class uploadFile extends AsyncTask<String,Integer,String>  {
        public String imagePath;


        public uploadFile( String imagePath) {

          this.imagePath= imagePath;
        }

        public uploadFile() {

        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.setProgress(progress [0]);
        }

        @Override
        protected String doInBackground(String... params) {
            return upload();
        }

        private String upload() {
            String responce ="";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);
                FileBody fileBodypath = new FileBody(new File(imagePath));
                FileBody image = new FileBody(new File(savestorage(imageBitmap) + "/video.png"));
                CustomMultiPartEntity cust = new CustomMultiPartEntity(new CustomMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });
                cust.addPart("fileupload", fileBodypath);
                cust.addPart("thumb", image);
                totalSize = cust.getContentLength();
                httppost.setEntity(cust);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                responce = EntityUtils.toString(resEntity);
            } catch (Exception e) {
                e.printStackTrace();
                responce = e.toString();
            }
            return responce;
                }


        }

    private String savestorage(Bitmap imageBitmap) {
        ContextWrapper crw = new ContextWrapper(getApplicationContext());
        File directory =crw.getDir("imagedir", Context.MODE_PRIVATE);
        File mypath = new File(directory,"video.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return directory.getAbsolutePath();
    }


    private void selectFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (item) {
            case "images":
                intent = new Intent();
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                break;
            case "Video":
                intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 1);
                break;
            case "Audio":
                intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select audieo"), 1);
                break;
            case "pdf":
                intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf/*");
                startActivityForResult(Intent.createChooser(intent, "Select pdf"), 1);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            // OI FILE Manager
            String selectedImageUriPath = selectedImageUri.getPath();
            imagePath = GetFilePathFromDevice.getPath(this, selectedImageUri);
            System.out.println("check the image path"+imagePath);
            if (imagePath != null) {
                File f = new File(imagePath);
                f.getName();
                select_button.setEnabled(true);
                tv.setText(" Title:- " + f.getName());
                tv.setEnabled(true);
                tv.setVisibility(View.VISIBLE);
                imageBitmap = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                imageview.setImageBitmap(imageBitmap);


            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         item =parent.getItemAtPosition(position).toString();




        //Toast.makeText(parent.getContext(),"selected:"+item,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}

//}
