package com.example.buscandohogar.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buscandohogar.R;
import com.example.buscandohogar.model.entity.User;
import com.example.buscandohogar.model.network.AppCallback;
import com.example.buscandohogar.model.repositories.UsuarioRepositorios;
import com.example.buscandohogar.view.Utils.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static com.example.buscandohogar.view.fragments.AdoptionFragment.CODIGO_CARRETE;
import static com.example.buscandohogar.view.fragments.AdoptionFragment.CODIGO_TOMAR_FOTO;

public class RegistrarDatosActivity extends AppCompatActivity {

    private static final String USUARIO_EDITAR = "usuario";
    private static String TAG = "RegistroDatos";
    private static String USERS_COLLECTION = "users";
    private TextInputLayout txtNombres, txtApellidos, txtTelefono, txtDepartamento, txtMunicipio,
            txtDireccion, txtEmail, txtContraseña, txtConfirmarContraseña;
    private TextView tvTitleForm, tvErrorImgProfile;
    private Button btnContinuar, btnAtras;
    private User usuarioEditar;
    private Uri uriProfileImageEdit;

    private String nombres, apellidos, departamento, municipio, direccion, email, contraseña, confirmarContraseña, telefono;
    private AutoCompleteTextView dropdownDepartamento;
    private UsuarioRepositorios usuarioRepositorios;
    private Uri uriProfileImage;
    private ImageView ivFotoProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_datos);
        Intent intent = getIntent();
        if( intent != null )
            usuarioEditar = (User) intent.getSerializableExtra(USUARIO_EDITAR);

        setDatos();
    }

    private void setDatos() {
        usuarioRepositorios = new UsuarioRepositorios(this);
        ivFotoProfile = findViewById(R.id.ivFotoProfile);
        tvTitleForm = findViewById(R.id.tvTitleForm);
        tvErrorImgProfile = findViewById(R.id.tvErrorImgProfile);
        txtNombres = findViewById(R.id.txtNombres);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtTelefono = findViewById(R.id.txtPhone);
        txtDepartamento = findViewById(R.id.txtDepartamento);
        txtMunicipio = findViewById(R.id.txtMunicipio);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtEmail = findViewById(R.id.txtEmail);
        txtContraseña = findViewById(R.id.txtContraseña);
        txtConfirmarContraseña = findViewById(R.id.txtConfirmarContraseña);
        dropdownDepartamento = findViewById(R.id.dropdownDepartamento);
        btnContinuar = findViewById(R.id.btnContinuar);
        btnAtras = findViewById(R.id.btnAtras);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        RegistrarDatosActivity.this,
                        R.layout.dropdown_menu_item,
                        getResources().getStringArray(R.array.departamentos));
        dropdownDepartamento.setAdapter(adapter);

        if( usuarioEditar != null ){
            setearInfoUsuarioEditar(usuarioEditar);
        }

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * Metodo encargado de setear la informacion en los campos, para actualizar el usuario
     * @param usuarioEditar
     */
    private void setearInfoUsuarioEditar(User usuarioEditar) {
        txtNombres.getEditText().setText(usuarioEditar.getName());
        txtApellidos.getEditText().setText(usuarioEditar.getLastname());
        txtTelefono.getEditText().setText(usuarioEditar.getPhone());
        txtDepartamento.getEditText().setText(usuarioEditar.getDepartamento());
        txtMunicipio.getEditText().setText(usuarioEditar.getMunicipio());
        txtDireccion.getEditText().setText(usuarioEditar.getAddress());

        Glide.with(this)
                .load(usuarioEditar.getUrlImagen())
                .into(ivFotoProfile);

        tvTitleForm.setText(getString(R.string.editar_info, "Usuario"));
        btnContinuar.setText(getString(R.string.modificar_info));
        txtEmail.setVisibility(View.GONE);
        txtContraseña.setVisibility(View.GONE);
        txtConfirmarContraseña.setVisibility(View.GONE);

    }

    /**
     * Meotod para validar los campos del formulario
     */
    private boolean validarInfoRegistro(){
        boolean valido = true;
        if( txtNombres.getEditText().getText().toString().trim().isEmpty() ){
            txtNombres.setError(getString(R.string.errorNombre));
            valido = false;
        } else {
            txtNombres.setError(null);
        }
        if( txtApellidos.getEditText().getText().toString().trim().isEmpty() ){
            txtApellidos.setError(getString(R.string.errorApellidos));
            valido = false;
        } else {
            txtApellidos.setError(null);
        }
        if( txtMunicipio.getEditText().getText().toString().trim().isEmpty() ){
            txtMunicipio.setError(getString(R.string.errorMunicipio));
            valido = false;
        } else {
            txtMunicipio.setError(null);
        }
        if( txtDepartamento.getEditText().getText().toString().trim().isEmpty() ){
            txtDepartamento.setError(getString(R.string.errorDepartamento));
            valido = false;
        } else {
            txtDepartamento.setError(null);
        }
        if( txtDireccion.getEditText().getText().toString().trim().isEmpty() ){
            txtDireccion.setError(getString(R.string.errorDireccion));
            valido = false;
        } else{
            txtDireccion.setError(null);
        }
        if( txtTelefono.getEditText().getText().toString().trim().isEmpty() ){
            txtTelefono.setError(getString(R.string.errorTelefono));
            valido = false;
        } else if ( txtTelefono.getEditText().getText().toString().trim().length() != 10 ){
            txtTelefono.setError(getString(R.string.mensaje_error_telefono));
            valido = false;
        } else {
            txtTelefono.setError(null);
        }

        if( usuarioEditar == null ){
            if( contraseña.trim().isEmpty() ){
                txtContraseña.setError(getString(R.string.errorContraseña));
                valido = false;
            } else {
                txtContraseña.setError(null);
            }

            if( confirmarContraseña.toString().trim().isEmpty() ){
                txtConfirmarContraseña.setError(getString(R.string.errorContraseña));
                valido = false;
            } else {
                txtConfirmarContraseña.setError(null);
            }

            if( !contraseña.trim().isEmpty() && !confirmarContraseña.trim().isEmpty() ){
                if( !contraseña.equals(confirmarContraseña) ){
                    txtConfirmarContraseña.setError(getString(R.string.errorContraseñasNoCoinciden));
                    txtContraseña.setError(getString(R.string.errorContraseñasNoCoinciden));
                    valido = false;
                } else {
                    txtConfirmarContraseña.setError(null);
                    txtContraseña.setError(null);
                }
            }
            if ( uriProfileImage == null ){
                tvErrorImgProfile.setVisibility(View.VISIBLE);
                valido = false;
            } else {
                tvErrorImgProfile.setVisibility(View.INVISIBLE);
            }
        }


        return valido;
    }

    /**
     * Metodo encargado de Registrar o Editar la informaicon de un usuario
     */
    public void registerUser(View view){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher_round);
        progressDialog.setMessage("Por favor espere...");
        Log.d(TAG, "registerUser: "+ usuarioEditar );

        if( usuarioEditar == null ){
            nombres = txtNombres.getEditText().getText().toString();
            apellidos = txtApellidos.getEditText().getText().toString();
            departamento = txtDepartamento.getEditText().getText().toString();
            municipio = txtMunicipio.getEditText().getText().toString();
            direccion = txtDireccion.getEditText().getText().toString();
            email = txtEmail.getEditText().getText().toString();
            telefono = txtTelefono.getEditText().getText().toString();
            contraseña = txtContraseña.getEditText().getText().toString();
            confirmarContraseña = txtConfirmarContraseña.getEditText().getText().toString();

            final User user = new User(nombres, apellidos, email, direccion, telefono, departamento, municipio);

            if( validarInfoRegistro() ){
                progressDialog.show();
                String ruta = uriProfileImage.toString();
                String imagen = ruta.substring(ruta.lastIndexOf("/"));
                usuarioRepositorios.subirImagenUsuario(imagen, uriProfileImage, new AppCallback<String>() {
                    @Override
                    public void correcto(String respuesta) {
                        user.setUrlImagen(respuesta);
                        usuarioRepositorios.crearUsuario(user, contraseña, new AppCallback<Boolean>() {
                            @Override
                            public void correcto(Boolean respuesta) {
                                progressDialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }
                            @Override
                            public void error(Exception exception) {
                                progressDialog.show();
                                procesarMensajeError(exception);
                            }
                        });
                    }

                    @Override
                    public void error(Exception exception) {
                        progressDialog.show();
                        procesarMensajeError(exception);
                    }
                });
            } else {
                progressDialog.dismiss();
            }

        } else {
            usuarioEditar.setName(txtNombres.getEditText().getText().toString());
            usuarioEditar.setLastname(txtApellidos.getEditText().getText().toString());
            usuarioEditar.setDepartamento(txtDepartamento.getEditText().getText().toString());
            usuarioEditar.setMunicipio(txtMunicipio.getEditText().getText().toString());
            usuarioEditar.setAddress(txtDireccion.getEditText().getText().toString());
            usuarioEditar.setPhone(txtTelefono.getEditText().getText().toString());

            if( validarInfoRegistro() ){
                progressDialog.show();
                if( uriProfileImageEdit != null ){
                    String ruta = uriProfileImageEdit.toString();
                    String imagen = ruta.substring(ruta.lastIndexOf("/"));
                    usuarioRepositorios.subirImagenUsuario(imagen, uriProfileImageEdit, new AppCallback<String>() {
                        @Override
                        public void correcto(String respuesta) {
                            usuarioEditar.setUrlImagen(respuesta);
                            usuarioRepositorios.editarUsuario(usuarioEditar, new AppCallback<Boolean>() {
                                @Override
                                public void correcto(Boolean respuesta) {
                                    Log.d(TAG, "correcto: "+ respuesta.toString());
                                    progressDialog.dismiss();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                                @Override
                                public void error(Exception exception) {
                                    progressDialog.show();
                                    procesarMensajeError(exception);
                                }
                            });
                        }

                        @Override
                        public void error(Exception exception) {
                            progressDialog.show();
                            procesarMensajeError(exception);
                        }
                    });
                } else {
                    usuarioRepositorios.editarUsuario(usuarioEditar, new AppCallback<Boolean>() {
                        @Override
                        public void correcto(Boolean respuesta) {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void error(Exception exception) {
                            progressDialog.show();
                            procesarMensajeError(exception);
                        }
                    });
                }
            } else {
                progressDialog.dismiss();
            }
        }
    }

    /**
     * Metodo encargado de procesar los mensajes de error.
     * Obteniendo la exception se actualiza la interfaz
     * @param exception
     */
    public void procesarMensajeError(Exception exception) {
        Log.d(TAG, "procesarMensajeError: "+ exception.toString());
        if( exception instanceof FirebaseAuthInvalidCredentialsException){
            switch ( ((FirebaseAuthInvalidCredentialsException) exception).getErrorCode() ){
                case "ERROR_INVALID_EMAIL":
                    txtEmail.setError(getString(R.string.errorEmail));
                    txtEmail.requestFocus();
                    break;
                case "ERROR_WEAK_PASSWORD":
                    txtContraseña.setError(getString(R.string.error_pass_weak));
                    txtContraseña.requestFocus();
                    break;
                default:
                    break;
            }
        } else if( exception instanceof FirebaseAuthUserCollisionException){
            if( ((FirebaseAuthUserCollisionException) exception).getErrorCode().equals("ERROR_CREDENTIAL_ALREADY_IN_USE") ||
                    ((FirebaseAuthUserCollisionException) exception).getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") ||
                        ((FirebaseAuthUserCollisionException) exception).getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")){
                txtEmail.setError(getString(R.string.errorEmailExiste));
                txtEmail.requestFocus();
            }
        }
    }

    public void tomarFoto(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if( intent.resolveActivity(this.getPackageManager()) != null ){
            File foto = null;
            try{
                foto = crearFoto();
            }catch (IOException e){
                e.printStackTrace();
            }
            if( foto != null ){
                if( usuarioEditar != null ){
                    uriProfileImageEdit = FileProvider.getUriForFile(this,
                            "com.example.buscandohogar.fileprovider", foto);
                }else {
                    uriProfileImage = FileProvider.getUriForFile(this,
                            "com.example.buscandohogar.fileprovider", foto);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriProfileImage);
                startActivityForResult(intent, CODIGO_TOMAR_FOTO);
            }

        }

    }

    private File crearFoto() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File foto = File.createTempFile(
                timeStamp,
                ".jpg",
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        );
        return foto;
    }

    public void buscarCarrete(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CODIGO_CARRETE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK ){
            switch (requestCode){
                case CODIGO_TOMAR_FOTO:
                    if (usuarioEditar != null) {
                        Glide.with(this).load(uriProfileImageEdit).into(ivFotoProfile);
                    } else {
                        Glide.with(this).load(uriProfileImage).into(ivFotoProfile);
                    }
                    break;
                case CODIGO_CARRETE:
                    if( usuarioEditar != null ){
                        uriProfileImageEdit = data.getData();
                        Glide.with(this).load(uriProfileImageEdit).into(ivFotoProfile);
                    } else {
                        uriProfileImage = data.getData();
                        Glide.with(this).load(uriProfileImage).into(ivFotoProfile);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}