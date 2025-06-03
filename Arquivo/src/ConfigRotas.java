import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigRotas {

    private String ConfigCaminho = "C:\\Teste\\Configuracao\\config.txt";
    private String ConfigLinha1 = "Processado=C:\\Teste\\Processado";
    private String ConfigLinha2 = "Nao Processado=C:\\Teste\\NaoProcessado";

    public void VerificaCaminho() {
        Path caminho = Paths.get(ConfigCaminho).getParent();
        Path raiz = caminho.getParent();

        if (raiz == null || !Files.exists(raiz)) {
            JOptionPane.showMessageDialog(null, "Erro, não existe raíz: " + raiz);
        }

        if (!Files.exists(caminho)) {
            JOptionPane.showMessageDialog(null, "Erro, não existe caminho: " + caminho);
        }
    }

    public void CriarCaminho() {
        Path caminho = Paths.get(ConfigCaminho).getParent();

        try {
            Files.createDirectories(caminho);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar diretórios de configuração");
        }
    }

    public void VerificarConfig() throws IOException {
        BufferedReader leitor = new BufferedReader(new FileReader(ConfigCaminho));
        String linha1 = leitor.readLine();
        String linha2 = leitor.readLine();

        if (linha1 == null || linha2 == null || linha1.isEmpty() || linha2.isEmpty()
                || !linha1.equals(ConfigLinha1) || !linha2.equals(ConfigLinha2)) {
            JOptionPane.showMessageDialog(null, "Erro, configuração vazia ou configuração diferente");
            try (PrintWriter out = new PrintWriter(new FileWriter(ConfigCaminho, false))) {
                out.println(ConfigLinha1);
                out.println(ConfigLinha2);
                VerificarArquivoConfig();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar o arquivo");
            }
        }
    }

    private void VerificarArquivoConfig() {
        boolean arquivoExiste = (new File(ConfigCaminho)).exists();

        if (!arquivoExiste) {
            try {
                PrintWriter out = new PrintWriter(ConfigCaminho);
                out.println(ConfigLinha1);
                out.println(ConfigLinha2);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao criar o arquivo de configuração");
            }
        }
    }

    private boolean PastasJaExistem() {
        Path pasta1 = Paths.get("C:\\Teste\\Processado");
        Path pasta2 = Paths.get("C:\\Teste\\NaoProcessado");
        return Files.exists(pasta1) && Files.exists(pasta2);
    }

    public void LerArquivoConfig() {
        if (PastasJaExistem()) {
            return;
        }

        try {
            CriarCaminho();
            VerificarArquivoConfig();
            VerificarConfig();

            try (BufferedReader leitor = new BufferedReader(new FileReader(ConfigCaminho))) {
                String linha1 = leitor.readLine();
                String linha2 = leitor.readLine();

                String PastaProcessado = linha1.split("=")[1].trim();
                String PastaNaoProcessado = linha2.split("=")[1].trim();

                Files.createDirectories(Paths.get(PastaProcessado));
                Files.createDirectories(Paths.get(PastaNaoProcessado));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao ler o arquivo de configuração.");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro crítico: " + e.getMessage());
        }
    }
}
