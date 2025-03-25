package screens;

import screens.navigation.Navigation;
import screens.navigation.Route;

import java.util.Scanner;

public abstract class AbstractScreen implements Screen {
    protected final Navigation navigation;

    protected AbstractScreen(Navigation navigation) {
        this.navigation = navigation;
    }

    @Override
    public final void display() {
        System.out.println();
        showOptions();
        System.out.print(">");

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        onOptionSelected(option);
    }

    protected abstract void showOptions();

    protected abstract void onOptionSelected(int option);
}
