package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth auth;
    private String email, name, con;
    private FloatingActionButton eab, sab;
    private OnFragmentInteractionListener mListener;
    private CircleImageView circleImageView;
    private TextView username, emailadd, contact, editpin, changePic;
    private EditText editname, editcontact;
    private Uri mImageUri;
    private UploadTask uploadTask;
    private ProgressDialog progressDialog;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(String param1, String param2) {

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){

        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null)
            email = users.getEmail();

        eab = (FloatingActionButton) view.findViewById(R.id.fragmentprofilebtnEdit);
        sab = (FloatingActionButton) view.findViewById(R.id.fragmentprofilebtnSave);
        circleImageView = (CircleImageView) view.findViewById(R.id.fragmentprofilepic);
        username = (TextView) view.findViewById(R.id.fragmentprofileusername);
        emailadd = (TextView) view.findViewById(R.id.fragmentprofileemailadd);
        contact = (TextView) view.findViewById(R.id.fragmentprofilecontact);
        editpin = (TextView) view.findViewById(R.id.fragmentprofileresetpin);
        editname = (EditText) view.findViewById(R.id.fragmentprofileeditname);
        editcontact = (EditText) view.findViewById(R.id.fragmentprofileeditcontact);
        changePic = (TextView) view.findViewById(R.id.fragmentprofilechangePic);

        getter(email);
        getProfilePic(email);
        emailadd.setText(String.format("Email Address: %s", email));

        eab.setOnClickListener(view12 -> {

            sab.setVisibility(View.VISIBLE);
            eab.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            emailadd.setVisibility(View.GONE);
            contact.setVisibility(View.GONE);
            editpin.setVisibility(View.VISIBLE);
            editname.setVisibility(View.VISIBLE);
            editname.setText(name);
            editcontact.setVisibility(View.VISIBLE);
            editcontact.setText(con);
            changePic.setVisibility(View.VISIBLE);
        });

        sab.setOnClickListener(view13 -> {

            sab.setVisibility(View.GONE);
            eab.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            emailadd.setVisibility(View.VISIBLE);
            contact.setVisibility(View.VISIBLE);
            setUserFullName(email, editname.getText().toString().trim());
            setContactno(email, editcontact.getText().toString().trim());
            startActivity(new Intent(getActivity(), Mainmenu.class));
        });

        editpin.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), newpin.class)));

        changePic.setOnClickListener(view14 -> fileChooser.launch(new String[]{"image/*"}));

    }

    private void setUserFullName(String email, String newname){
        String[] split = email.split("@");
        try{
            FirebaseDatabase.getInstance().getReference("Users").child(split[0]).child("fullname").setValue(newname);
        }
        catch(Exception e){
            Log.e("SetUserFName", e.getMessage(), e);
        }
    }

    private void setContactno(String email, String newcontact){
        String[] split = email.split("@");
        try{
            FirebaseDatabase.getInstance().getReference("Users").child(split[0]).child("phone").setValue(newcontact);
            editpin.setVisibility(View.GONE);
            editname.setVisibility(View.GONE);
            editcontact.setVisibility(View.GONE);
            changePic.setVisibility(View.GONE);
        }
        catch(Exception e){

            Log.e("SetUserFContact", e.getMessage(), e);
        }
    }

    public void getter(String email){

        String[] split = email.split("@");
        DatabaseReference g = FirebaseDatabase.getInstance().getReference().child("Users");
        g.child(split[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username.setText("Name: "+snapshot.child("fullname").getValue(String.class));
                name = snapshot.child("fullname").getValue(String.class);
                contact.setText("Contact No: "+snapshot.child("phone").getValue(String.class));
                con = snapshot.child("phone").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("on getter (ProfileFragment): "+error);
            }
        });
    }

    public void getProfilePic(String email){

        String[] split = email.split("@");
        DatabaseReference g = FirebaseDatabase.getInstance().getReference().child("Users").child(split[0]).child("ProfilePicture");
        g.child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String url = snapshot.getValue(String.class);
                Glide.with(getActivity()).load(url).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("on getProfilePic (ProfileFragment): "+error);
            }
        });
    }

    ActivityResultLauncher<String[]> fileChooser = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            try{
                mImageUri = result;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View root = getLayoutInflater().inflate((R.layout.dialogconfirmupdateprofile), null);
                CircleImageView c = root.findViewById(R.id.dialogprofilepic);
                c.setImageURI(mImageUri);
                builder.setView(root);
                builder.setPositiveButton("Confirm", (dialogInterface, i) -> uploadFile());
                builder.setNegativeButton("Cancel", ((dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }));
                AlertDialog alert = builder.create();
                alert.show();
            }
            catch(Exception e){
                System.err.println("on fileChooser: "+e);
            }
        }
    });

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        System.out.println(mime.getExtensionFromMimeType(cR.getType(uri)));
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Upload in progress");
        progressDialog.setIcon(R.drawable.sentinel);
        progressDialog.show();
        String[] split = email.split("@");
        String a = split[0] + "." + getFileExtension(mImageUri);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("ProfilePictures/"+a);

        try{
            if(mImageUri != null){
                uploadTask = storageRef.putFile(mImageUri);
                circleImageView.setImageURI(mImageUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        throw Objects.requireNonNull(task.getException());
                    }
                    return storageRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();
                        UploadImage upload = new UploadImage(split[0], miUrlOk);
                        FirebaseDatabase.getInstance().getReference("Users").child(split[0]).child("ProfilePicture").setValue(upload);
                    }
                    else {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        System.err.println("Uploading of profile picture is unsuccessful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        System.err.println("on uploadPic Failure: "+e);
                    }
                });
            }
            else {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(getActivity(), "Image is not Found!", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e){
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            Log.e("UploadFile", e.getMessage(), e);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
