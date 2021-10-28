import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;

public class ClientProfessor {

	public static void main(String[] args) throws IOException {

		Socket socket = new Socket("localhost", 35502);
		Scanner sc = new Scanner(System.in);

		try {

			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			DataInputStream input = new DataInputStream(socket.getInputStream());
			ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());
			
			/* Cliente que chamou o Servidor - 1 representa o professor */
			output.write(1);

			System.out.println(" Olá, professor. Inicie a chamada digitando o numero da turma: ");
			int numeroTurma = sc.nextInt();
			/* Numero da Turma que é enviada para o servidor */
			output.write(numeroTurma);

			System.out.println(input.readUTF());

			System.out.println(" Para encerrar a chamada da turma pressione 0 (zero) \n ");
			int encerrar = sc.nextInt();

			while (0 != encerrar) {
				System.out.println(" Para encerrar a chamada da turma pressione 0 (zero) \n");
				encerrar = sc.nextInt();
			}

			/* Envia dados para o servidor encerrar a chamada */
			output.write(encerrar);
			output.write(numeroTurma);

			/* Alunos que responderam a chamada da turma fazendo distinção se nenhum, apenas
			 um ou vários alunos responderam a chamada */

			ArrayList<Integer> alunosResponderamChamada = new ArrayList<>();
			alunosResponderamChamada = (ArrayList<Integer>) inputObject.readObject();
			if (alunosResponderamChamada.isEmpty()) {
				System.out.println("Nenhum aluno respondeu a chamada da turma. \n Segue data e hora da validação da informação: "+ new Date()+".");
			} else {
				if (alunosResponderamChamada.size() == 1) {
					System.out.println("Apenas o aluno de matrícula: " + alunosResponderamChamada.get(0)
							+ " respondeu a chamada da turma. \n Segue data e hora da validação da informação: "+ new Date()+"." );
				} else {
					System.out.println("   Segue lista de matriculs !!! \n");
					for (int i = 0; i < alunosResponderamChamada.size(); i++) {
						System.out.println((i + 1) + ": " + alunosResponderamChamada.get(i));
					}
				}

				System.out.println( " A Chamada da turma " + numeroTurma + " foi encerrada!!! \n Segue data e hora da validação do final da chamada: "+ new Date()+".");
			}
		} catch (Exception e) {
			e.getMessage();
		} finally {
			socket.close();
			sc.close();
		}
	}
}
