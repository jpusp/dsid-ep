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
        System.out.println("\t[1] Listar peers");
        System.out.println("\t[2] Obter peers");
        System.out.println("\t[3] Listar arquivos locais");
        System.out.println("\t[4] Buscar arquivos");
        System.out.println("\t[5] Exibir estatísticas");
        System.out.println("\t[6] Alterar tamanho de chunk");
        System.out.println("\t[9] Sair");
    }

    @Override
    protected void onOptionSelected(int option) {
        Route route;
        switch (option) {
            case 1:
                route = Route.DISPLAY_PEERS;
                break;
            case 2:
                route = Route.GET_PEERS;
                break;
            case 3:
                route = Route.DISPLAY_FILES;
                break;
            case 4:
                route = Route.SEARCH_FILES;
                break;
            case 5:
                route = Route.SHOW_STATS;
                break;
            case 6:
                route = Route.CHANGE_CHUNK;
                break;
            case 9:
                route = Route.EXIT;
                break;
            default:
                route = Route.INVALID;
                break;
        }

        navigation.navigate(route);
    }
}
