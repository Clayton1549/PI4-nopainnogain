package com.example.littlewolf_pc.app.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.littlewolf_pc.app.R;
import com.example.littlewolf_pc.app.activity.InternalActivity;
import com.example.littlewolf_pc.app.model.UsuarioDTO;
import com.example.littlewolf_pc.app.resource.ApiUsuario;
import com.example.littlewolf_pc.app.utils.UsuarioSingleton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private EditText etEmail;
    private EditText etSenha;
    private Button btnLogin;
    private Button btnRegistro;
    UsuarioDTO getLoggedUser;


    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_refactor, container, false);
            etEmail = v.findViewById(R.id.etEmail);
            etSenha = v.findViewById(R.id.etSenha);
            btnLogin = v.findViewById(R.id.btnLogin);
            btnRegistro = v.findViewById(R.id.btnRegistro);

        final Dialog loading = new Dialog(getContext(), android.R.style.Theme_Black);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.loading_dialog, null);
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading.getWindow().setBackgroundDrawableResource(R.color.transparent);
        loading.setContentView(view);
        loading.setCancelable(false);

        final  SharedPreferences prefs = getActivity().getSharedPreferences("usuario", MODE_PRIVATE);

        Integer id = prefs.getInt("id", 0);
        String email = prefs.getString("email", null);
        String senha = prefs.getString("senha", null);


        if(id > 0 && email != null && senha != null){
            Intent intent = new Intent(getActivity(), InternalActivity.class);
            startActivity(intent);
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etEmail.getText().toString().isEmpty()){
                    Snackbar.make(getView(), getResources().getString(R.string.email_required), Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(etSenha.getText().toString().isEmpty()){
                    Snackbar.make(getView(), getResources().getString(R.string.password_required), Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(etEmail.getText().toString() != null){
                    boolean emailValido = validate(etEmail.getText().toString());
                    if(!emailValido){
                    Snackbar.make(getView(), getResources().getString(R.string.valid_email), Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                    }
                }

                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

                if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_access), Toast.LENGTH_LONG).show();

                    return;
                }


                    loading.show();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://josiasveras.azurewebsites.net")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                UsuarioDTO usuarioDTO = new UsuarioDTO();

                usuarioDTO.setEmail(etEmail.getText().toString());
                usuarioDTO.setSenha(etSenha.getText().toString());


                ApiUsuario apiUsuario =
                        retrofit.create(ApiUsuario.class);
                Call<UsuarioDTO> usuarioDTOCall = apiUsuario.login(usuarioDTO);

                Callback<UsuarioDTO> usuarioDTOCallback = new Callback<UsuarioDTO>() {
                    @Override
                    public void onResponse(Call<UsuarioDTO> call, Response<UsuarioDTO> response) {
                        getLoggedUser = response.body();

                        if(getLoggedUser != null && response.code() == 200){

                            SharedPreferences.Editor editor = prefs.edit();

                            editor.putInt("id", getLoggedUser.getId());
                            editor.putString("email", getLoggedUser.getEmail());
                            editor.putString("senha", getLoggedUser.getSenha());
                            editor.putString("nome", getLoggedUser.getNome());
                            editor.putString("foto", getLoggedUser.getFoto());

                            editor.apply();

                            loading.dismiss();

                            etEmail.setText("");
                            etSenha.setText("");

                            UsuarioSingleton.getInstance().setUsuario(getLoggedUser);

                            Intent intent = new Intent(getActivity(), InternalActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                    }
                    @Override
                    public void onFailure(Call<UsuarioDTO> call, Throwable t) {

                        loading.dismiss();
                        etSenha.setText("");
                        Snackbar.make(getView(), getResources().getString(R.string.user_password_invalid), Snackbar.LENGTH_SHORT)
                                .show();
                        t.printStackTrace();

                    }
                };
                usuarioDTOCall.enqueue(usuarioDTOCallback);

            }
        };


        View.OnClickListener listener2 = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //criando fragmento de cadastro
                CadastroFragment cf = new CadastroFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.moldura, cf).commit();
            }
       };

        btnLogin.setOnClickListener(listener);
        btnRegistro.setOnClickListener(listener2);


        return v;
    }

    private void mensagem(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder (LoginFragment.this.getActivity());
        //Configura o corpo da mensagem
        builder.setMessage(message);
        //Configura o título da mensagem
        builder.setTitle(title);
        //Impede que o botão seja cancelável (possa clicar
        //em voltar ou fora para fechar)
        builder.setCancelable(false);
        //Configura um botão de OK para fechamento (um
        //outro listener pode ser configurado no lugar do "null")
        builder.setPositiveButton("OK", null);
        //Cria efetivamente o diálogo
        AlertDialog dialog = builder.create();
        //Faz com que o diálogo apareça na tela
        dialog.show();


    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

}
