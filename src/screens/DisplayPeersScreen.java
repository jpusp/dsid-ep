package screens;

import screens.navigation.Navigation;
import screens.navigation.Route;

import java.util.Scanner;

public class DisplayPeersScreen extends AbstractScreen {

    public DisplayPeersScreen(Navigation navigation) {
        super(navigation);
    }

    @Override
    protected void showOptions() {
        System.out.println("Lista de peers:");
        System.out.println("\t\t[0] Voltar para o menu anterior");
    }

    @Override
    protected void onOptionSelected(int option) {
        if (option == 0) {
            navigation.navigate(Route.INITIAL);
        } else {

        }
    }
}
