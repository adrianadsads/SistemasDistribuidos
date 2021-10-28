import java.net.*;
import java.io.*;
//import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;

public class ClientProfessor {

	public static void main(String[] args) throws IOException {

		Socket socket = new Socket("localhost", 35502);
		Scanner sc = new Scanner(System.in);

		try {

			
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			ObjectInputStream inputObject = new ObjectInputStream(socket.getInputStream());
			// Cliente chamando o Servidor - 1 representa o professor
			output.write(1);

			System.out.println(" Saudações, professor. Comece a chamada digitando o numero da turma: ");
			int numeroTurma = sc.nextInt();
			// 1) O numero da Turma do aluno
			output.write(numeroTurma);

			System.out.println(new Date() + "  A chamada da turma " + numeroTurma + " foi iniciada... \n\n");

			System.out.println(" Para finalizar a chamada da turma pressione 0 (zero) \n ");
			int encerrar = sc.nextInt();

			while (0 != encerrar) {
				System.out.println(" Para encerrar a chamada da turma pressione 0 (zero) \n");
				encerrar = sc.nextInt();
			}

			// 2) A chamada foi encerrada;
			output.write(encerrar);
			output.write(numeroTurma);

			// 3) Alunos que responderam a chamada da turma

			ArrayList<Integer> alunosResponderamChamada = new ArrayList<>();
			alunosResponderamChamada = (ArrayList<Integer>) inputObject.readObject();
			if (alunosResponderamChamada.isEmpty()) {
				System.out.println("Nenhum aluno respondeu a chamada da turma");
			} else {
				if (alunosResponderamChamada.size() == 1) {
					System.out.println("Apenas o aluno de matrícula: " + alunosResponderamChamada.get(0)
							+ " respondeu a chamada da turma");
				} else {
					System.out.println(" Segue lista de matriculas !!! \n");
					for (int i = 0; i < alunosResponderamChamada.size(); i++) {
						System.out.println((i + 1) + ": " + alunosResponderamChamada.get(i));

					}
				}

				System.out.println(new Date() + " A Chamada da turma " + numeroTurma + " foi encerrada!!!");
			}
		} catch (Exception e) {
			e.getMessage();
		} finally {
			socket.close();
			sc.close();
		}
	}
}