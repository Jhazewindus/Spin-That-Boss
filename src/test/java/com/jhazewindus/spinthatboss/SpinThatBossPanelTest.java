package com.jhazewindus.spinthatboss;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpinThatBossPanelTest
{
	@Test
	public void hostCanSearchBossesAndStartLootValueRun() throws Exception
	{
		AtomicReference<RunSetup> started = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() ->
		{
			SpinThatBossPanel panel = new SpinThatBossPanel(started::set, () -> { }, () -> { }, new RunSetup());
			panel.showState(null, "", "", true, false);
			button(panel, "Setup run").doClick();
			JTextField search = components(panel, JTextField.class).stream()
				.filter(field -> "Search bosses".equals(field.getToolTipText())).findFirst().get();
			search.setText("zul");
			assertTrue(check(panel, "Zulrah").isVisible());
			assertFalse(check(panel, "Vorkath").isVisible());
			button(panel, "Select all").doClick();
			assertTrue(check(panel, "Vorkath").isSelected());
			button(panel, "Clear all").doClick();
			check(panel, "Zulrah").doClick();
			JComboBox<?> goals = components(panel, JComboBox.class).get(0);
			for (int i = 0; i < goals.getItemCount(); i++)
			{
				if ("First to loot-value target".equals(goals.getItemAt(i).toString()))
				{
					goals.setSelectedIndex(i);
				}
			}
			components(panel, JSpinner.class).get(1).setValue(500_000L);
			button(panel, "Start run").doClick();
		});
		assertNotNull(started.get());
		assertEquals("FIRST_VALUE", started.get().goalType);
		assertEquals(500_000L, started.get().valueTarget);
		assertEquals(1, started.get().bosses.size());
		assertEquals("Zulrah", started.get().bosses.get(0));
	}

	private static JButton button(Container root, String text)
	{
		return components(root, JButton.class).stream().filter(item -> text.equals(item.getText())).findFirst().get();
	}

	private static JCheckBox check(Container root, String text)
	{
		return components(root, JCheckBox.class).stream().filter(item -> text.equals(item.getText())).findFirst().get();
	}

	private static <T extends Component> List<T> components(Container root, Class<T> type)
	{
		List<T> found = new ArrayList<>();
		for (Component component : root.getComponents())
		{
			if (type.isInstance(component))
			{
				found.add(type.cast(component));
			}
			if (component instanceof Container)
			{
				found.addAll(components((Container) component, type));
			}
		}
		return found;
	}
}
