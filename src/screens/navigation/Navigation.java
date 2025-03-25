package screens.navigation;

import screens.DisplayPeersScreen;
import screens.InitialScreen;
import screens.Screen;

public class Navigation {

    public void navigate(Route route) {
        switch (route) {
            case INITIAL -> {
                navigateToScreen(new InitialScreen(this));
            }
            case DISPLAY_PEERS -> {
                navigateToScreen(new DisplayPeersScreen(this));
            }
            case GET_PEERS -> {
                System.out.println("Obtendo peers...");
            }
            case DISPLAY_FILES -> {
                System.out.println("Listando arquivos locais...");
            }
            case SEARCH_FILES -> {
                System.out.println("Buscando arquivos...");
            }
            case SHOW_STATS -> {
                System.out.println("Exibindo estatísticas...");
            }
            case CHANGE_CHUNK -> {
                System.out.println("Alterando tamanho de chunk...");
            }
            case EXIT -> {
                System.out.println("Saindo...");
                System.exit(0);
            }
            default -> {
                System.out.println("Comando inválido.");
            }
        }

    }

    private void navigateToScreen(Screen screen) {
        screen.display();
    }
}
