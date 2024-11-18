import java.awt.EventQueue;
import javax.swing.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import java.util.Map;

public class MiConversor {

    private JFrame frame;
    private JTextField txt;
    private JButton btn;
    private JComboBox<Moneda> cmb;
    private JLabel lbl;
    private double dolar, euro, libra, yen;

    public enum Moneda {
        pesos_dolar,
        pesos_euro,
        pesos_libra,
        pesos_yen
    }

    public double valorInput = 0.00;

    // Tu clave de API
    private static final String API_KEY = "165408799d513908723b6278"; // Reemplaza esto con tu clave real
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/MXN"; // URL de la API

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MiConversor window = new MiConversor();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MiConversor() {
        initialize();
        obtenerTasasCambio(); // Cargar tasas de cambio al inicio
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        txt = new JTextField();
        txt.setBounds(10, 11, 123, 20);
        frame.getContentPane().add(txt);
        txt.setColumns(10);

        cmb = new JComboBox<>(Moneda.values());
        cmb.setBounds(10, 59, 123, 22);
        frame.getContentPane().add(cmb);

        btn = new JButton("Convertir");
        btn.addActionListener(e -> Convertir());
        btn.setBounds(161, 59, 89, 23);
        frame.getContentPane().add(btn);

        lbl = new JLabel("00.00");
        lbl.setBounds(161, 11, 89, 20);
        frame.getContentPane().add(lbl);
    }

    public void Convertir() {
        if (Validar(txt.getText())) {
            Moneda moneda = (Moneda) cmb.getSelectedItem();
            switch (moneda) {
                case pesos_dolar:
                    PesosAMoneda(dolar);
                    break;
                case pesos_euro:
                    PesosAMoneda(euro);
                    break;
                case pesos_libra:
                    PesosAMoneda(libra);
                    break;
                case pesos_yen:
                    PesosAMoneda(yen);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + moneda);
            }
        }
    }

    public void PesosAMoneda(double moneda) {
        double res = valorInput / moneda;
        lbl.setText(Redondear(res));
    }

    public String Redondear(double valor) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(valor);
    }

    public boolean Validar(String texto) {
        try {
            double x = Double.parseDouble(texto);
            if (x > 0) {
                valorInput = x;
                return true;
            }
        } catch (NumberFormatException e) {
            lbl.setText("Solamente números !!");
        }
        return false;
    }

    private void obtenerTasasCambio() {
        try {
            String urlString = API_URL; // URL de la API
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Parsear la respuesta JSON
            Gson gson = new Gson();
            Map<String, Object> responseMap = gson.fromJson(response.toString(), Map.class);
            Map<String, Double> rates = (Map<String, Double>) responseMap.get("rates");
            dolar = rates.get("USD");
            euro = rates.get("EUR");
            libra = rates.get("GBP");
            yen = rates.get("JPY"); // Obtener la tasa para yen

        } catch (Exception e) {
            e.printStackTrace();
            lbl.setText("Error al obtener tasas de cambio.");
        }
    }
}
