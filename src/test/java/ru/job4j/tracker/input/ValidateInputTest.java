package ru.job4j.tracker.input;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidateInputTest {

    @Test
    public void whenInvalidInput() {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(mem));
        String[] data = {"one", "1"};
        ValidateInput input = new ValidateInput(new StubInput(data));
        input.askInt("Enter");
        assertThat(mem.toString()).isEqualTo(String.format("Please enter validate data again.%n"));
        System.setOut(out);
    }

    @Test
    public void whenInvalidMenuKey() {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(mem));
        String[] data = {"1", "0"};
        ValidateInput input = new ValidateInput(new StubInput(data));
        input.askInt("Enter", 1);
        assertThat(mem.toString()).isEqualTo(String.format("Please select key from menu.%n"));
        System.setOut(out);
    }
}