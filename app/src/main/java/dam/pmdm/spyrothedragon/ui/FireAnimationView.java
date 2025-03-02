package dam.pmdm.spyrothedragon.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class FireAnimationView extends View {

    private Paint firePaint; // Pintura para la llama
    private float flameHeight = 0; // Altura de la llama (para animación)
    private Random random; // Para generar valores aleatorios

    public FireAnimationView(Context context) {
        super(context);
        init();
    }

    public FireAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Configurar la pintura para la llama
        firePaint = new Paint();
        firePaint.setColor(Color.RED);
        firePaint.setStyle(Paint.Style.FILL);
        random = new Random();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Coordenadas de la base de la llama
        int baseX = getWidth() / 2; // Centrado en la vista
        int baseY = getHeight();
        // Dibujar la llama como una serie de círculos superpuestos
        for (int i = 0; i < 5; i++) {
            int flameWidth = 50 + i * 10; // Ancho de la llama
            int flameHeight = (int) (this.flameHeight - i * 20); // Altura de la llama
            // Cambiar el color de la llama para simular el fuego
            firePaint.setColor(Color.argb(255, 255, 100 + i * 20, 0));

            // Dibujar un círculo para cada parte de la llama
            canvas.drawCircle(baseX, baseY - flameHeight, flameWidth / 2, firePaint);
        }
    }

    // Método para actualizar la altura de la llama
    public void setFlameHeight(float height) {
        this.flameHeight = height;
        invalidate(); // Redibujar la vista
    }

    // Método para generar un efecto de fuego dinámico
    public void animateFlame() {
        flameHeight = random.nextInt(100) + 50; // Altura aleatoria
        invalidate(); // Redibujar la vista
    }
}