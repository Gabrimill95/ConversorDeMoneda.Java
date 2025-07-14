package conversor;

public class Conversion {
    private String fecha;
    private String origen;
    private String destino;
    private double monto;
    private double resultado;

    public Conversion(String fecha, String origen, String destino, double monto, double resultado) {
        this.fecha = fecha;
        this.origen = origen;
        this.destino = destino;
        this.monto = monto;
        this.resultado = resultado;
    }

    // Getters (obligatorios para Gson si querés deserializar después)
    public String getFecha() { return fecha; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public double getMonto() { return monto; }
    public double getResultado() { return resultado; }
}
