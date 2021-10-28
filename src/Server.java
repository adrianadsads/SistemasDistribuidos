import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class Server extends Thread {

	
	private static ArrayList<ListaPresenca> chamadasAbertas;
	private Integer matricula;
	private Integer numTurma;

	private static ArrayList<Integer> turmaAberta;
	private static ArrayList<Integer> listaDeAlunos;

	private Socket server = null;
	private DataInputStream input;

	private ObjectOutputStream outputObject;

	public Server(Socket socket) throws IOException {
		this.server = socket;
		input = new DataInputStream(socket.getInputStream());

	}

	public void run() {

		try {

			DataInputStream input = new DataInputStream(server.getInputStream());
			DataOutputStream output = new DataOutputStream(server.getOutputStream());
			ObjectOutputStream outputObject = new ObjectOutputStream(server.getOutputStream());

			int cliente = input.read();

			if (1 == cliente) {
				System.out.println(new Date() + " >>> O Professor foi conectado ao servidor. A turma foi iniciada \n");

				runClientProfessor(server, input, output, outputObject);
			}

			else if (2 == cliente) {
				System.out.println(new Date() + " >>> O Aluno foi conectado ao servidor. \n");
				runClientAluno(server, input, output);
			}

		} catch (IOException e) {
			e.getMessage();
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		int portaServer = 35502;

		System.out.println(" Servidor iniciado  na Porta: " + portaServer + " \n Data e hora: " + new Date() + "  \n");

		System.out.println("  ***** Aguardando conexão dos clientes ***** \n");

		listaDeAlunos = new ArrayList<>();
		turmaAberta = new ArrayList<>();
		chamadasAbertas = new ArrayList<>();

		while (true) {

			try {

				ServerSocket serverSocket = new ServerSocket(portaServer);
				serverSocket.setSoTimeout(1000000);
				Socket socket = serverSocket.accept();
				Server server = new Server(socket);
				server.start();

				Thread.sleep(2000);
				serverSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void runClientAluno(Socket server, DataInputStream inputAluno, DataOutputStream outputAluno) {

		try {
			// 1) A matricula do aluno
			int matricula = inputAluno.read();

			// 2) O numero da Turma
			int numTurma = inputAluno.read();
			int codResposta = 0;

			for (int i = 0; i < chamadasAbertas.size(); i++) {
				if (chamadasAbertas.get(i).getMatricula() == matricula) {

					System.out.println("Essa Matrícula já foi Registrada!!! \n Data e hora da validação da informação: "
							+ new Date());

					outputAluno.write(codResposta);
					outputAluno
							.writeUTF("Essa Matrícula já foi Registrada!!! \n Data e hora da validação da informação: "
									+ new Date());
					return;

				}

			}

			if (!turmaAberta.contains(numTurma)) {

				System.out.println("   Atenção!!! A chamada para essa turma não foi aberta! "
						+ "  \n Segue data e hora da validação da informação: " + new Date());
				outputAluno.write(codResposta);

				outputAluno.writeUTF("   Atenção!!! A chamada para essa turma não foi aberta! "
						+ "  \n Segue data e hora da validação da informação: " + new Date());
				return;
			}

			ListaPresenca adicionaAlunoTurma = new ListaPresenca(matricula, numTurma);

			listaDeAlunos.add(matricula);

		
			codResposta = 1;
			chamadasAbertas.add(adicionaAlunoTurma);
			
			outputAluno.write(codResposta);
			System.out.println("  Presença Registrada com Sucesso!!! \n  Segue data e hora da validação da presença: "
					+ new Date());
			outputAluno.writeUTF("  Presença Registrada com Sucesso!!! \n  Segue data e hora da validação da presença: "
					+ new Date());


		} catch (IOException e) {
			e.getMessage();
		} catch (Exception e) {
			e.getMessage();
		}

	}

	private void runClientProfessor(Socket server, DataInputStream input, DataOutputStream output,
			ObjectOutputStream outputObject) throws IOException {

		int turma = -1;
		// 1) Numero da Turma
		turma = input.read();

		if (turma != -1) {
			turmaAberta.add(turma);

			System.out.println(new Date() + " >>> A chamada da turma " + turma + " foi aberta pelo professor. \n");

			// 2) A chamada foi encerrada pelo professor;
			Integer finalizar = input.read();
			Integer numTurma = input.read();

			ArrayList<Integer> dados;
			dados = new ArrayList<>();

			// 3) Envia uma lista com a matricula dos alunos que responderam a chamada

			if (finalizar == 0) {

				for (int i = 0; i < chamadasAbertas.size(); i++) {

					if (chamadasAbertas.get(i).getNumTurma().equals(numTurma)) {

						dados.add(chamadasAbertas.get(i).getMatricula());
					}
				}

				outputObject.writeObject(dados);

				turmaAberta.remove(numTurma);

				System.out.println(new Date() + " >>> A chamada da turma " + turma + " foi encerrado. \n");

			}

		} else {

			System.out.println(new Date() + " >>> O numero da turma informada é inválida.");
		}

	}
}
