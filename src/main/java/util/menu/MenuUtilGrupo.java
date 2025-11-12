package main.java.util.menu;

import main.java.model.Grupo;

import java.util.ArrayList;

public class MenuUtilGrupo {

	public static Grupo chooseGrupoFromLista(ArrayList<Grupo> grupos) {

		String header = "\n==== Grupos ====\n";

		String[] menuOptions = new String[grupos.size() + 1];
		for (int i = 0; i < grupos.size(); i++) {
			Grupo grupoAux = grupos.get(i);
			menuOptions[i] = grupoAux.getNome() + " (ID: " + grupoAux.getId() + ")";
			if (i == grupos.size() - 1) {
				menuOptions[i] = menuOptions[i].concat("\n");
			}
		}
		menuOptions[grupos.size()] = "Retonar ao menu anteior";

		Grupo grupoEscolhido = null;

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			if (opt == menuOptions.length - 1) {
				return null;
			}

			return grupos.get(opt);
		}
	}

}
