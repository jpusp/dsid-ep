package screens;

import screens.navigation.Navigation;
import screens.navigation.Route;

public class InitialScreen extends AbstractScreen {

    public InitialScreen(Navigation navigation) {
        super(navigation);
    }

    @Override
    protected void showOptions() {
        System.out.println("Escolha um comando:");
        System.out.println("\t\t[1] Listar peers");
        System.out.println("\t\t[2] Obter peers");
        System.out.println("\t\t[3] Listar arquivos locais");
        System.out.println("\t\t[4] Buscar arquivos");
        System.out.println("\t\t[5] Exibir estatísticas");
        System.out.println("\t\t[6] Alterar tamanho de chunk");
        System.out.println("\t\t[9] Sair");
    }

    @Override
    protected void onOptionSelected(int option) {
        Route route = switch (option) {
            case 1 -> Route.DISPLAY_PEERS;
            case 2 -> Route.GET_PEERS;
            case 3 -> Route.DISPLAY_FILES;
            case 4 -> Route.SEARCH_FILES;
            case 5 -> Route.SHOW_STATS;
            case 6 -> Route.CHANGE_CHUNK;
            case 9 -> Route.EXIT;
            default -> Route.INVALID;
        };

        navigation.navigate(route);
    }
}
