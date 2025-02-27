package com.example.enduranceacademyapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.enduranceacademyapp.LoginActivity;
import android.Manifest;
import com.example.enduranceacademyapp.R;
import com.example.enduranceacademyapp.data.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserFragment extends Fragment {

    private View view;
    private Button btnLogout, btnDeleteAccount;
    private RadioGroup radioFormatoFecha;
    private RadioButton radioSemanal, radioMensual;
    private TextView txtFormatoFecha;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_PERMISSION = 100;
    private ImageView userImageView;
    private SharedPrefManager sharedPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout correcto
        view = inflater.inflate(R.layout.activity_user, container, false);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        userImageView = view.findViewById(R.id.userImageView);


        // Enlazar vistas con el XML
        btnLogout = view.findViewById(R.id.logout);
        btnDeleteAccount = view.findViewById(R.id.delete_account);
        radioFormatoFecha = view.findViewById(R.id.radioFormatoFecha);
        txtFormatoFecha = view.findViewById(R.id.txtFormatoFecha);
        radioSemanal = view.findViewById(R.id.radioSemanal);
        radioMensual = view.findViewById(R.id.radioMensual);

        // Recuperar selección guardada de SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", 0);
        String formatoGuardado = prefs.getString("formato_calendario", "Semanal");

        // Aplicar el valor guardado
        if (formatoGuardado.equals("Semanal")) {
            radioSemanal.setChecked(true);
        } else {
            radioMensual.setChecked(true);
        }

        // Listener para guardar la selección del RadioGroup
        radioFormatoFecha.setOnCheckedChangeListener((group, checkedId) -> {
            String formatoSeleccionado;
            if (checkedId == R.id.radioSemanal) {
                formatoSeleccionado = "Semanal";
                Toast.makeText(getActivity(), "Formato semanal seleccionado", Toast.LENGTH_SHORT).show();
            } else {
                formatoSeleccionado = "Mensual";
                Toast.makeText(getActivity(), "Formato mensual seleccionado", Toast.LENGTH_SHORT).show();
            }

            // Guardar la selección en SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("formato_calendario", formatoSeleccionado);
            editor.apply();
        });

        loadUserImage();

        userImageView.setOnClickListener(v -> requestPermissions());

        // Configurar Listeners de botones
        btnLogout.setOnClickListener(v -> logout());
        btnDeleteAccount.setOnClickListener(v -> deleteAccount());


        return view;
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            } else {
                showImagePicker();
            }
        } else { // Para API 31 o menor
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            } else {
                showImagePicker();
            }
        }
    }



    private void showImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = Intent.createChooser(galleryIntent, "Selecciona una imagen");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        startActivityForResult(chooser, REQUEST_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                showImagePicker();
            } else {
                Toast.makeText(requireContext(), "Permisos denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                        saveUserImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == REQUEST_CAMERA && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                saveUserImage(photo);
            }
        }
    }

    private void saveUserImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        SharedPrefManager.saveUserImage(requireContext(), encodedImage);
        userImageView.setImageBitmap(bitmap);
    }

    private void loadUserImage() {
        String encodedImage = SharedPrefManager.getUserImage(requireContext());
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            userImageView.setImageBitmap(bitmap);
        }
    }

}
