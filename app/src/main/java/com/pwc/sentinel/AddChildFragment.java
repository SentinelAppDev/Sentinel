package com.pwc.sentinel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

public class AddChildFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;
    private FirebaseAuth auth, tmpAuth;
    private String email;
    private TextInputEditText childname, childemail;
    private Button btnRegister;
    private LinearLayoutCompat layout;
    private String fName = "", fEmail = "";
    private final String txtMsg = "Please click the link below to download the Sentinel Parental Control Application.";
    private final String subj = "Welcome to Sentinel: A Parental Control Application";
    private final String devemail = "sentinedevcontrol@gmail.com";
    private final String devpass = "hqonyoshgiwxxxnl";

    public AddChildFragment() {}

    public static AddChildFragment newInstance(String param1, String param2) {
        AddChildFragment fragment = new AddChildFragment();
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
        return inflater.inflate(R.layout.fragment_addchild, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        auth = FirebaseAuth.getInstance();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://sentinel-9991a-default-rtdb.asia-southeast1.firebasedatabase.app")
                .setApiKey("AIzaSyDvwXE1kLC0Cf2hfGsomAQATAcI66ollNc")
                .setApplicationId("sentinel-9991a").build();
        try {
            FirebaseApp fa = FirebaseApp.initializeApp(getActivity(), firebaseOptions, "Sentinel");
            tmpAuth = FirebaseAuth.getInstance(fa);
        }
        catch (IllegalStateException e){
            System.err.println("onIntantiatingtmpAuth: "+e);
        }
        FirebaseUser users = auth.getCurrentUser();
        if (users != null)
            email = users.getEmail();
        String[] split = email.split("@");

        layout = (LinearLayoutCompat) view.findViewById(R.id.fragmentaddchildlinearlayout);
        childemail = (TextInputEditText) view.findViewById(R.id.fragmentaddchildchildsemail);
        childname = (TextInputEditText) view.findViewById(R.id.fragmentaddchildchildsname);
        btnRegister = (Button) view.findViewById(R.id.fragmentaddchildregister);

        btnRegister.setOnClickListener(view1 -> {

            fEmail = Objects.requireNonNull(childemail.getText()).toString().trim();
            fName = Objects.requireNonNull(childname.getText()).toString().trim();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View root = getLayoutInflater().inflate((R.layout.dialogconfirmaddchild), null);
            TextView cemail = root.findViewById(R.id.confirmchildemail);
            TextView cname = root.findViewById(R.id.confirmchildname);
            cemail.setText(fEmail);
            cname.setText(fName);
            builder.setView(root);
            builder.setPositiveButton("Confirm", (dialogInterface, i) -> {

                if (TextUtils.isEmpty(fEmail)){
                    childemail.setError("Your child's email address is required.");
                }
                else if(TextUtils.isEmpty(fName)){
                    childname.setError("Your child's name is required.");
                }
                else if (TextUtils.isEmpty(fEmail) && TextUtils.isEmpty(fName)){
                    childemail.setError("Your child's email address is required.");
                    childname.setError("Your child's name is required.");
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(fEmail).matches()){
                    childemail.setError("Please input a valid email address.");
                }
                else {
                    tmpAuth.createUserWithEmailAndPassword(fEmail, split[0])
                            .addOnCompleteListener(getActivity(), task -> {
                                if(!task.isSuccessful()){
                                    Log.w("Add Child", "signInWithCredential", task.getException());
                                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                        final Snackbar sn = Snackbar.make(layout, "Child has already been added.", Snackbar.LENGTH_SHORT);
                                        sn.show();
                                        childname.setText(null);
                                        childemail.setText(null);
                                    }
                                    else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                        final Snackbar sn1 = Snackbar.make(layout, "Please check your email address.", Snackbar.LENGTH_SHORT);
                                        sn1.show();
                                        childname.setText(null);
                                        childemail.setText(null);
                                    }
                                }
                                else if(task.isSuccessful()) {
                                    createChildData(fName, fEmail, split[0]);
                                    createParentChildData(fName, split[0]);
                                    new Thread(() -> {
                                        try{
                                            GMailSender sender = new GMailSender(devemail, devpass);
                                            sender.sendMail(subj, txtMsg, devemail, fEmail);
                                        }
                                        catch(Exception e){
                                            Log.e("onAddChild Send Mail", e.getMessage(), e);
                                        }
                                    }).start();
                                    final Snackbar sn2 = Snackbar.make(layout, "Your child has been successfully added.", Snackbar.LENGTH_INDEFINITE);
                                    sn2.show();
                                    tmpAuth.signOut();
                                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                    ft.detach(AddChildFragment.this).attach(AddChildFragment.this).commit();
                                    childname.setText(null);
                                    childemail.setText(null);
                                }
                            });
                }
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                Snackbar sn3 = Snackbar.make(layout, "Cancelled", Snackbar.LENGTH_SHORT);
                sn3.show();
                childname.setText(null);
                childemail.setText(null);
            });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    private void createChildData(String name, String email, String pemail){
        try{
            Child c = new Child();
            c.setName(name);
            c.setDevicestatus("0");
            c.setEmail(email);
            c.setSavedLocation("0,0");
            c.setCurrentLocation("0,0");
            c.setMs("0");
            c.setFb("0");
            c.setIg("0");
            c.setYt("0");
            c.setTw("0");
            c.setTk("0");
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            databaseReference.child("Children").child(pemail).child(name).setValue(c)
                    .addOnSuccessListener(unused -> Log.d("on AddChild FirebaseData","Child data uploaded successfully."))
                    .addOnFailureListener(e -> {
                        Log.d("on AddChild FirebaseData","Child data upload failed.");
                        final Snackbar sn4 = Snackbar.make(layout, "on AddChild FirebaseData: "+e, Snackbar.LENGTH_INDEFINITE);
                        sn4.show();
                    });
        }

        catch(Exception e){
            System.err.println("on createChildData: "+e);
        }
    }

    private void createParentChildData(String name, String pemail){
        try{
            FirebaseDatabase fd = FirebaseDatabase.getInstance();
            DatabaseReference dr = fd.getReference();
            dr.child("Users").child(pemail).child("Children").child(name).setValue(true);

            String[] split = fEmail.split("@");
            FirebaseDatabase df = FirebaseDatabase.getInstance();
            DatabaseReference rd = df.getReference();
            rd.child("ChildrenParent").child(split[0]).setValue(pemail);

            FirebaseDatabase fg = FirebaseDatabase.getInstance();
            DatabaseReference tf = fg.getReference();
            tf.child("ChildName").child(split[0]).setValue(name);
        }
        catch(Exception e){
            System.err.println("on createParentChildData: "+e);
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
        if (context instanceof ProfileFragment.OnFragmentInteractionListener) {
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
