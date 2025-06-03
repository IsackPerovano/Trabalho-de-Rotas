import java.io.File;

public class Main {
    public static void main(String[] args) {

        ConfigRotas config = new ConfigRotas();
        config.LerArquivoConfig();

        Rotas rotas = new Rotas();
        rotas.LerArquivos();

    }
}