
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

public class ClientAluno {

	public static void main(String[] args) throws UnknownHostException, IOException {

		Socket socket = new Socket("localhost", 35502);
		Scanner sc = new Scanner(System.in);

		try {

			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());

			// O cliente chama o Servidor - 2 representa o aluno
			output.write(2);

			System.out.println(" Caro aluno, informe sua matricula: ");
			int matricula = sc.nextInt();
			System.out.println(" Informe o numero da sua turma: ");
			int numTurma = sc.nextInt();

			// 1) A matricula do Aluno
			output.write(matricula);
			// 2) O numero turma do aluno
			output.write(numTurma);

			// 3)Averigua se a presença do aluno foi lançada

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
