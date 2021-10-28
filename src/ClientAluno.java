import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientAluno {

	public static void main(String[] args) throws UnknownHostException, IOException {

		Socket socket = new Socket("localhost", 35502);
		Scanner sc = new Scanner(System.in);

		try {

			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());

			/* Cliente que chamou o Servidor - 2 representa o aluno */
			output.write(2);

			System.out.println(" Prezado aluno, informe sua matricula: ");
			int matricula = sc.nextInt();
			System.out.println(" Agora informe o numero da sua turma: ");
			int numTurma = sc.nextInt();

			/* 1) Matricula Aluno que será enviada para o servidor */
			output.write(matricula);
			/* 2) Numero turma ue será enviada para o servidor */
			output.write(numTurma);

			/* 3)Verifica se a presença foi registrada */

			/* Recebe do servidor */
			int cod = input.read();

			if (1 == cod)
				System.out.println(input.readUTF());
			else
				System.out.println(input.readUTF());

		} catch (Exception e) {
			e.getMessage();
		} finally {
			socket.close();
			sc.close();
		}
	}
}
