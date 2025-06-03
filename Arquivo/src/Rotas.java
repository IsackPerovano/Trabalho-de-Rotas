import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class Rotas {
    private String rotas = "C:\\Teste";
    private String processado = "C:\\Teste\\Processado";
    private String naoprocessado = "C:\\Teste\\NaoProcessado";
    private ArrayList<String> rotasArquivo = new ArrayList<>();

    public void LerArquivos() {
        try {
            File pasta = new File(rotas);
            for (String nomeArquivo : pasta.list()) {
                if (nomeArquivo.toLowerCase().endsWith(".txt")) {
                    rotasArquivo.add(nomeArquivo);
                }
            }

            for (String nome : rotasArquivo) {
                File arquivo = new File(rotas + "\\" + nome);
                ProcessarArquivo(arquivo);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao ler arquivos");
        }
    }

    public void ProcessarArquivo(File arquivo) {
        int somaHeader = 0;
        int countConexoes = 0;
        int countPesos = 0;
        int somaPesos = 0;
        String trailer = null;
        boolean headerValido = false;
        boolean moverParaNaoProcessado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String header = br.readLine();

            if (header == null || !header.startsWith("00") || header.length() != 9) {
                JOptionPane.showMessageDialog(null, "Header inválido no arquivo: " + arquivo.getName());
                moverParaNaoProcessado = true;
            } else {
                try {
                    somaHeader = Integer.parseInt(header.substring(4));
                    headerValido = true;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor inválido no header do arquivo: " + arquivo.getName());
                    moverParaNaoProcessado = true;
                }

                String linha;
                while (!moverParaNaoProcessado && (linha = br.readLine()) != null) {
                    if (linha.startsWith("01")) {
                        countConexoes += VerificarConexoes(linha);
                    } else if (linha.startsWith("02")) {
                        int peso = VerificarPeso(linha);
                        if (peso == 0) {
                            JOptionPane.showMessageDialog(null, "Linha de peso inválida no arquivo: " + arquivo.getName());
                            moverParaNaoProcessado = true;
                            break;
                        }
                        somaPesos += peso;
                        countPesos++;
                    } else if (linha.startsWith("09")) {
                        trailer = linha;
                    }
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao ler o arquivo: " + arquivo.getName() + "\n" + e.getMessage());
            moverParaNaoProcessado = true;
        }


        if (!headerValido || trailer == null || moverParaNaoProcessado) {
            MoverArquivo(arquivo, naoprocessado);
            return;
        }

        try {
            String[] partes = trailer.substring(2).split(";");
            int rc = Integer.parseInt(partes[0].split("=")[1]);
            int rp = Integer.parseInt(partes[1].split("=")[1]);
            int somaTrailer = Integer.parseInt(partes[2]);

            if (rc != countConexoes || rp != countPesos || somaTrailer != somaPesos || somaHeader != somaPesos) {
                JOptionPane.showMessageDialog(null, "Trailer inconsistente no arquivo: " + arquivo.getName());
                MoverArquivo(arquivo, naoprocessado);
            } else {
                MoverArquivo(arquivo, processado);
                JOptionPane.showMessageDialog(null, "Arquivo processado com sucesso: " + arquivo.getName());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao processar trailer do arquivo: " + arquivo.getName() + "\n" + e.getMessage());
            MoverArquivo(arquivo, naoprocessado);
        }
    }

    public int VerificarConexoes(String linha) {
        try {
            if (!linha.startsWith("01") || !linha.contains("=")) return 0;
            String dados = linha.substring(2);
            String[] partes = dados.split("=");

            if (partes.length != 2) return 0;

            String no = partes[0];
            String nd = partes[1];

            if (no.matches("[A-Z]{1,2}") && nd.matches("[A-Z]{1,2}") && !no.equals(nd)) {
                return 1;
            }

            return 0;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Resumo das conexões inválido");
            return 0;
        }
    }

    public int VerificarPeso(String linha) {
        try {
            if (!linha.startsWith("02") || !linha.contains(";") || !linha.contains("=")) return 0;
            String dados = linha.substring(2);
            String[] partes = dados.split("[=;]");
            if (partes.length != 3) return 0;

            String no = partes[0];
            String nd = partes[1];
            String pesoStr = partes[2];

            if (!no.matches("[A-Z]{1,2}") || !nd.matches("[A-Z]{1,2}") || no.equals(nd)) return 0;

            int peso = Integer.parseInt(pesoStr);
            if (peso < 0 || peso > 9999) return 0;

            return peso;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao ler linha de peso: " + linha);
            return 0;
        }
    }

    private void MoverArquivo(File arquivo, String destino) {
        try {
            Path origem = arquivo.toPath();
            Path destinoPath = Paths.get(destino, arquivo.getName());
            Files.createDirectories(destinoPath.getParent());
            Files.move(origem, destinoPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao mover o arquivo " + arquivo.getName() + ": " + e.getMessage());
        }
    }
}
