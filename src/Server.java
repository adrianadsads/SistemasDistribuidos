import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class Server extends Thread {

	private static ArrayList<ListaPresenca> chamadasAbertas;
	private static ArrayList<Integer> turmaAberta;

	private Socket server = null;

	public Server(Socket socket) throws IOException {
		this.server = socket;

	}

	public void run() {

		try {

			DataInputStream input = new DataInputStream(server.getInputStream());
			DataOutputStream output = new DataOutputStream(server.getOutputStream());
			ObjectOutputStream outputObject = new ObjectOutputStream(server.getOutputStream());

			int cliente = input.read();

			if (1 == cliente) {
				System.out.println(
						" O Professor foi conectado ao servidor. A turma foi iniciada \n Segue data e hora da valida��o da informa��o: "
								+ new Date() + ".");

				runClientProfessor(server, input, output, outputObject);
			}

			else if (2 == cliente) {
				System.out
						.println(" O Aluno foi conectado ao servidor. \n Segue data e hora da valida��o da informa��o: "
								+ new Date() + ".");
				runClientAluno(server, input, output);
			}

		} catch (IOException e) {
			e.getMessage();
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		int portaServer = 35502;

		System.out.println(" Servidor iniciado  na Porta: " + portaServer + " \n data e hora: " + new Date() + ".  \n");

		System.out.println("  ***** Aguardando conex�o dos clientes ***** \n");

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
			/* 1) Matricula aluno recebida do Cliente Aluno */
			int matricula = inputAluno.read();

			/* 2) Numero Turma recebida do Cliente Aluno */
			int numTurma = inputAluno.read();
			int codResposta = 0;

			for (int i = 0; i < chamadasAbertas.size(); i++) {
				if (chamadasAbertas.get(i).getMatricula() == matricula) {

					System.out.println(
							"Essa Matr�cula j� foi Registrada!!! \n Segue data e hora da valida��o da informa��o: "
									+ new Date() + ".");

					/* Envia para o Cliente Aluno o codResposta */
					outputAluno.write(codResposta);
					/* Sa�da do do texto para o Cliente Aluno */
					outputAluno.writeUTF(
							"Essa Matr�cula j� foi Registrada!!! \n Segue data e hora da valida��o da informa��o: "
									+ new Date() + ".");
					return;

				}

			}

			if (!turmaAberta.contains(numTurma)) {
				System.out.println(
						"   Aten��o!!! A chamada para essa turma n�o foi aberta ou j� foi finalizada pelo professor!"
								+ "  \n Segue data e hora da valida��o da informa��o: " + new Date() + ".");

				/* Sa�da do codResposta para o Cliente Aluno */
				outputAluno.write(codResposta);

				/* Sa�da do do texto para o Cliente Aluno */
				outputAluno.writeUTF(
						"   Aten��o!!! A chamada para essa turma n�o foi aberta ou j� foi finalizada pelo professor! "
								+ "  \n Segue data e hora da valida��o da informa��o: " + new Date() + ".");
				return;
			}

			/* Cria o objeto aluno com matricula e numTurma */
			ListaPresenca adicionaAlunoTurma = new ListaPresenca(matricula, numTurma);

			/* Modifica o valor de codResposta */
			codResposta = 1;

			/* Adiciona no array chamadasAbertas o objeto criado acima adiconando o aluno */
			chamadasAbertas.add(adicionaAlunoTurma);
			/* Envia o codResposta para o cliente Aluno */
			outputAluno.write(codResposta);
			System.out.println("  Presen�a Registrada com Sucesso!!! \n  Segue data e hora da valida��o da presen�a: "
					+ new Date() + ".");

			/* Envia o texto para o aluno */
			outputAluno.writeUTF("  Presen�a Registrada com Sucesso!!! \n  Segue data e hora da valida��o da presen�a: "
					+ new Date() + ".");

		} catch (IOException e) {
			e.getMessage();
		} catch (Exception e) {
			e.getMessage();
		}

	}

	private void runClientProfessor(Socket server, DataInputStream input, DataOutputStream output,
			ObjectOutputStream outputObject) throws IOException {

		int turma = -1;
		/* Receber do Cliente Professor o N�mero da Turma */
		turma = input.read();

		/* Adiciona a turma recebida pelo Cliente professor */
		if (turma != -1) {
			turmaAberta.add(turma);

			output.writeUTF(" A chamada da turma " + turma
					+ " foi aberta pelo professor. \n Segue data e hora da valida��o da abertura da chamada "
					+ new Date() + ".");
			System.out.println(" A chamada da turma " + turma
					+ " foi aberta pelo professor. \n Segue data e hora da valida��o da abertura da chamada "
					+ new Date() + ".");

			/* Recebe os dados para finalizar a Chamada */
			Integer finalizar = input.read();
			Integer numTurma = input.read();

			/*
			 * Array que recebera a lista de matr�culas dos alunos que responderam a lista
			 * da chamada
			 */

			ArrayList<Integer> dados;
			dados = new ArrayList<>();

			/* Checagem para adicionar no array dados as matriculas */

			if (finalizar == 0) {

				for (int i = 0; i < chamadasAbertas.size(); i++) {

					if (chamadasAbertas.get(i).getNumTurma().equals(numTurma)) {

						dados.add(chamadasAbertas.get(i).getMatricula());
					}
				}

				/*
				 * Envia o objeto para o Cliente Professor com o array de matr�culas dos alunos
				 * que responderam a chamada
				 */
				outputObject.writeObject(dados);

				/* remove a turma que foi finalizada no array turmaAberta */
				turmaAberta.remove(numTurma);

				/*
				 * checa as matr�culas que responderam a chamada comparando com numTurma e
				 * remove o objeto do array
				 */

				for (int i = 0; i < chamadasAbertas.size(); i++) {

					if (chamadasAbertas.get(i).getNumTurma().equals(numTurma)) {

						chamadasAbertas.remove(i);
						i = i - 1;
					}
				}

				System.out.println(" A chamada da turma " + turma
						+ " foi encerrada. \n Segue data e hora da valida��o da informa��o: " + new Date() + ".");

			}

		} else {

			System.out.println(
					" Numero da turma inv�lida. \n Segue data e hora da valida��o da informa��o: " + new Date() + ".");
		}

	}

}
