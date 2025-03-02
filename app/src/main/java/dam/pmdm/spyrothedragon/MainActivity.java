package dam.pmdm.spyrothedragon;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.PrincipalBinding;
import dam.pmdm.spyrothedragon.ui.Listener;

public class MainActivity extends AppCompatActivity implements Listener {
    private SharedPreferences sharedPreferences;
    private int contador = 0;
    private ActivityMainBinding binding;
    private PrincipalBinding guideBinding;
    private NavController navController;
    private boolean skipGuide;
    private View infoView;
    private View overlay;
    private View bocadillo;
    private int menuActual = 0;
    private MediaPlayer mediaPlayer;
    private View circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        skipGuide = sharedPreferences.getBoolean("skipGuide", false);
        // Si el usuario no ha saltado la guía, mostramos el tutorial
        if (!skipGuide) {
            mostrarTutorial();
        }

    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters)
            navController.navigate(R.id.navigation_characters);
        else if (menuItem.getItemId() == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds);
        else
            navController.navigate(R.id.navigation_collectibles);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_info);
        if (item != null) {
            if (skipGuide) {
                item.setEnabled(true); // Deshabilita el ítem "Info"
            } else {
                item.setEnabled(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }

    @Override
    public void Circle(int menuItemId) {
        int menuItemIndex = 0;
        if (menuItemId == R.id.nav_characters) {
            menuItemIndex = 0;
        } else if (menuItemId == R.id.nav_worlds) {
            menuItemIndex = 1;
        } else if (menuItemId == R.id.nav_collectibles) {
            menuItemIndex = 2;
        }
        mostrarCirculoInferior(menuItemIndex);
    }


    private void mostrarTutorial() {
        for (int i = 0; i < binding.navView.getMenu().size(); i++) {
            MenuItem menuItem = binding.navView.getMenu().getItem(i);
            menuItem.setEnabled(false);  // Deshabilita el ítem
        }
        overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE);
        guideBinding = binding.principalin;
        guideBinding.guideLayout.setVisibility(View.GONE);
        binding.navView.setOnItemSelectedListener(null);  // Desactiva el listener
        Button btnNext1 = findViewById(R.id.startButton);
        btnNext1.setVisibility(View.VISIBLE);
        View portadaGuia = findViewById(R.id.portadaguia);
        portadaGuia.setVisibility(View.VISIBLE);
        //listener clic de iniciar la guia
        btnNext1.setOnClickListener(v -> {
           mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.noti);
            mediaPlayer.start();
            portadaGuia.setVisibility(View.GONE);
            guideBinding.guideLayout.setVisibility(View.VISIBLE);
            // Muestra la guía en el primer elemento del menú (personajes por defecto)
            Circle(R.id.nav_characters);
            // Configura el botón de siguiente en la guía inicial
            Button btnNext = findViewById(R.id.nextButton);
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setOnClickListener(v1 -> {
                contador++;  // Aumenta el contador en cada clic
                mediaPlayer.start();
                // Condicional para manejar la visualización de varios bocadillos
                if (contador == 1) {
                    navegarFragmento();
                    showBocadillo(getString(R.string.bocadillo2));  // Primer bocadillo
                } else if (contador == 2) {
                    navegarFragmento();
                    hideBocadillos();  // Ocultar el primer bocadillo
                    showBocadillo(getString(R.string.bocadillo3));  // Segundo bocadillo
                } else if (contador == 3) {
                    circleView.setVisibility(View.GONE);
                    hideBocadillos();  // Ocultar el segundo bocadillo
                    infoView = findViewById(R.id.action_info);
                    Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up_down);
                    // Aplicar la animación a la vista del ítem del menú
                    infoView.startAnimation(scaleAnimation);
                    moveBocadilloToTop();
                    showBocadillo(getString(R.string.bocadillo4));
                } else if (contador == 4) {
                    infoView.clearAnimation();
                    hideBocadillos();
                    guideBinding.guideLayout.setVisibility(View.GONE);
                    View resumen = findViewById(R.id.resumenguia);
                    resumen.setVisibility(View.VISIBLE);

                }
            });

        });
        //si pulsamos el boton saltar
        Button btnSkip = findViewById(R.id.saltarButton);
        btnSkip.setOnClickListener(v2 -> {
            cerrarTutorial();
        });
        Button btnComen = findViewById(R.id.ComenzarBoton);
        btnComen.setOnClickListener(v2 -> {
            mediaPlayer.start();
            cerrarTutorial();
        });
        // Inicializa el primer bocadillo
        bocadillo = guideBinding.getRoot().findViewById(R.id.infoBubble);
        bocadillo.setVisibility(View.VISIBLE);
        bocadillo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_scale));
    }


    // Método para mostrar un bocadillo con un mensaje específico
    private void showBocadillo(String message) {
        bocadillo.setVisibility(View.VISIBLE);
        TextView bocadilloText = bocadillo.findViewById(R.id.infoBubble);  // Asegúrate de tener un TextView en el bocadillo
        bocadilloText.setText(message);  // Establecer el mensaje del bocadillo
        bocadillo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_scale));  // Animación de aparición
    }

    // Método para ocultar el bocadillo
    private void hideBocadillos() {
        if (bocadillo != null) {
            bocadillo.setVisibility(View.GONE);  // Oculta el bocadillo
        }
    }

    private void mostrarCirculoInferior(int menuItemIndex) {
         circleView = findViewById(R.id.circleView);
        circleView.setVisibility(View.VISIBLE);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int menuItemCount = binding.navView.getMenu().size();
        int centerX = (screenWidth / menuItemCount) * menuItemIndex + (screenWidth / (menuItemCount * 2));
        binding.navView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.navView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int navViewHeight = binding.navView.getHeight();
                int centerY = screenHeight - navViewHeight - circleView.getHeight();

                circleView.setX(Math.max(0, Math.min(centerX - (circleView.getWidth() / 2), screenWidth - circleView.getWidth())));
                circleView.setY(Math.max(0, Math.min(centerY, screenHeight - circleView.getHeight())));
            }
        });
        circleView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_scale));
    }



    private void navegarFragmento() {
        // Crear un objeto NavOptions para configurar las animaciones
        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)  // Animación de entrada
                .setExitAnim(R.anim.slide_out_left)   // Animación de salida
                // Animación de salida al volver
                .build();

        // Comprobar en qué menú estamos y decidir cuál es el siguiente
        switch (menuActual) {
            case 0:  // Si estamos en el primer menú
                if (navController != null) {
                    navController.navigate(R.id.navigation_worlds, null, navOptions);  // Navegar con animaciones
                    menuActual = 1;  // Actualizar al siguiente menú
                } else {
                    Log.e("NavigationError", "NavController es null");
                }
                break;

            case 1:  // Si estamos en el segundo menú
                if (navController != null) {
                    navController.navigate(R.id.navigation_collectibles, null, navOptions);  // Navegar con animaciones
                } else {
                    Log.e("NavigationError", "NavController es null");
                }
                break;

            default:
                Log.e("NavigationError", "No hay menu");
                break;
        }
    }

    private void moveBocadilloToTop() {
        if (bocadillo != null) {
            // Obtener las dimensiones de la pantalla
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int screenHeight = displayMetrics.heightPixels;
            // Establecer la posición del bocadillo en la parte superior de la pantalla
            bocadillo.setY(0); // Mueve el bocadillo a la parte superior (coordenada Y = 0)
            bocadillo.setVisibility(View.VISIBLE);
        }
    }

    private void cerrarTutorial() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("skipGuide", true);  // Guardar que se ha saltado la guía
        editor.apply();
        for (int i = 0; i < binding.navView.getMenu().size(); i++) {
            MenuItem menuItem = binding.navView.getMenu().getItem(i);
            menuItem.setEnabled(true);  // Deshabilita el ítem
            guideBinding.guideLayout.setVisibility(View.GONE);
            View overlay = findViewById(R.id.overlay);
            overlay.setVisibility(View.GONE);
            binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);
            mediaPlayer.release(); // Liberar recursos del MediaPlayer
            recreate();

        }
    }


}
