package com.pwc.sentinel;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnMapsSdkInitializedCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;
    private GoogleMap mMap;
    private FirebaseAuth auth;
    private DatabaseReference ref;
    private TextInputEditText setLoc;
    private AppCompatButton btnSave;
    private AppCompatSpinner mapspin;
    final List<String> names = new ArrayList<String>();
    private Circle mCircle;
    Marker marker;
    private String email = "", lat = "", lng = "", curChild = "", savedLoc = "", currentLoc = "";
    LatLng setloc;
    double l, lg, newl, newlg;

    public MapFragment() {}

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);
        MapsInitializer.initialize(getActivity(), Renderer.LATEST, this);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentmap);
        map.getMapAsync((OnMapReadyCallback) this);
        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null)
            email = users.getEmail();
        setLoc = (TextInputEditText) view.findViewById(R.id.fragmentmapsetAddress);
        btnSave = (AppCompatButton) view.findViewById(R.id.fragmentmapsave);
        mapspin = (AppCompatSpinner) view.findViewById(R.id.fragmentmapspinner);
        ref = FirebaseDatabase.getInstance().getReference();
        startTimer();
        btnSave.setOnClickListener(view1 -> {
            try{
                mCircle.remove();
                new GetCoordinates().execute(setLoc.getText().toString().replace(" ", "+"));
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.detach(MapFragment.this).attach(MapFragment.this).commit();
            }
            catch(Exception e){
                System.err.println("onClickSave: "+e);
            }
        });
        try{
            String[] split = email.split("@");
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
            dr.child("Users").child(split[0]).child("Children").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot n : snapshot.getChildren()){
                        String a = n.getKey();
                        names.add(a);
                        ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, names);
                        namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mapspin.setAdapter(namesAdapter);
                        mapspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                TextView tv =(TextView) view;
                                curChild = tv.getText().toString();
                                mMap.clear();
                                DatabaseReference rd = FirebaseDatabase.getInstance().getReference();
                                rd.child("Children").child(split[0]).child(curChild).child("savedLocation").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if(snapshot != null){
                                            savedLoc = snapshot.getValue(String.class);
                                            String[] s = savedLoc.split(",");
                                            double l1 = Double.parseDouble(s[0]);
                                            double l2 = Double.parseDouble(s[1]);
                                            LatLng ne = new LatLng(l1, l2);
                                            moveCamera(ne);
                                        }
                                        else{
                                            System.err.println("on retrieve saved location: no data yet");
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        System.err.println("onCancelled Get Saved Location: "+error);
                                    }
                                });
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                System.out.println("Nothing is selected on Spinner.");
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("onCancelled Get children names: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("onRetrieveNames: "+e);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try{
            mMap = googleMap;
            mMap.clear();
            LatLng l = new LatLng(7.057372234123851, 125.59362354953784);
        }
        catch(Exception e){
            Log.e("onMapReady", e.getMessage(), e);
        }
    }

    private void moveCamera(LatLng latLng){
        try{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            drawMarkerWithCircle(latLng);
        }
        catch(Exception e){
            Log.e("onMoveCamera", e.getMessage(), e);
        }
    }

    private void moveCameraTitle(Double a, Double b, String title){
        try{
            LatLng c = new LatLng(a,b);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 16));
            marker = mMap.addMarker(new MarkerOptions().position(c).title(title).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.childicon)));
        }
        catch(Exception e){
            Log.e("onMoveCameraTitle", e.getMessage(), e);
        }
    }

    private void drawMarkerWithCircle(LatLng position){
        try{
            double radiusInMeters = 200;
            int strokeColor = 0xffff0000;
            int shadeColor = 0x44ff0000;
            CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
            mCircle = mMap.addCircle(circleOptions);
        }
        catch(Exception e){
            Log.e("onDrawCircle", e.getMessage(), e);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

    }

    private class GetCoordinates extends AsyncTask<String,Void,String> {
        String[] split = email.split("@");
        ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyCs4_0DqOT5KuQ1ARw3YCeYVygaKhD6ODs",address);
                System.out.println(url);
                response = http.getHTTPData(url);
                return response;
            }
            catch (Exception ex)
            {
                Log.e("GeoFragments", ex.getMessage(), ex);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            System.out.println(s);
            try{
                JSONObject jsonObject = new JSONObject(s);

                lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();
                System.out.println(String.format("Coordinates : %s / %s ",lat,lng));
                l = Double.parseDouble(lat);
                lg = Double.parseDouble(lng);
                setloc = new LatLng(l,lg);
                moveCamera(setloc);
                System.out.println("Latitude:"+lat+ "and Longitude:"+lng+ ","+"User:"+split[0]+" and Email is:"+email);
                ref.child("Children").child(split[0]).child(curChild).child("savedLocation").setValue(lat+","+lng);
                if(dialog.isShowing())
                    dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private TimerTask timerTask;
    public void startTimer() {
        Timer timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 5000, 10000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                updateLocation();
            }
        };
    }

    private void updateLocation(){
        String[] split = email.split("@");
        DatabaseReference getLoc = FirebaseDatabase.getInstance().getReference();
        getLoc.child("Children").child(split[0]).child(curChild).child("currentLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    currentLoc = snapshot.getValue(String.class);
                    String[] s = currentLoc.split(",");
                    newl = Double.parseDouble(s[0]);
                    newlg = Double.parseDouble(s[1]);
                    moveCameraTitle(newl, newlg, "Current Location");
                }
                catch(Exception e){
                    System.err.println("onupdateLocation: "+e);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("onCancelled updateLocation: "+error);
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                Log.d("MapFragmentRender", "The latest version of the renderer is used.");
                break;
            case LEGACY:
                Log.d("MapFragmentRender", "The legacy version of the renderer is used.");
                break;
        }

    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
