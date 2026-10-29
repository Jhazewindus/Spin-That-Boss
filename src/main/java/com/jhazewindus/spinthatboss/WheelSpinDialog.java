package com.jhazewindus.spinthatboss;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.GridLayout;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** A short, on-screen wheel animation used for test spins and live spins. */
final class WheelSpinDialog
{
	static void show(Component parent, List<String> bosses, String winner, String title)
	{
		if (bosses == null || bosses.isEmpty() || winner == null) return;
		List<String> entries = new ArrayList<>(bosses);
		Collections.shuffle(entries);
		entries.removeIf(winner::equals);
		entries.add(0, winner);
		final int wheelSize = entries.size();
		Window owner = javax.swing.SwingUtilities.getWindowAncestor(parent);
		JDialog dialog = new JDialog(owner, title);
		dialog.setModal(false);
		dialog.setLayout(new BorderLayout(8, 8));
		JLabel result = new JLabel("The wheel is spinning…", SwingConstants.CENTER);
		result.setFont(result.getFont().deriveFont(Font.BOLD, 15f));
		Wheel wheel = new Wheel(entries);
		JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
		center.add(wheel);
		javax.swing.JTextArea selected = new javax.swing.JTextArea(String.join("\n", bosses));
		selected.setEditable(false);
		selected.setLineWrap(true);
		selected.setWrapStyleWord(true);
		javax.swing.JPanel list = new javax.swing.JPanel(new BorderLayout(4, 4));
		list.add(new JLabel("Selected bosses (" + bosses.size() + ")"), BorderLayout.NORTH);
		list.add(new javax.swing.JScrollPane(selected), BorderLayout.CENTER);
		center.add(list);
		dialog.add(center, BorderLayout.CENTER);
		dialog.add(result, BorderLayout.SOUTH);
		dialog.setSize(720, 440);
		dialog.setLocationRelativeTo(owner);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		long start = System.currentTimeMillis();
		Timer timer = new Timer(16, null);
		timer.addActionListener(e -> {
			double elapsed = (System.currentTimeMillis() - start) / 2600.0;
			double t = Math.min(1.0, elapsed);
			double eased = 1.0 - Math.pow(1.0 - t, 4);
			wheel.setRotation(eased * 6 * 360 - 360.0 / wheelSize / 2.0);
			if (t >= 1.0)
			{
				timer.stop();
				result.setText("Selected: " + winner);
			}
		});
		timer.start();
	}

	private static final class Wheel extends JPanel
	{
		private final List<String> entries;
		private double rotation;

		private Wheel(List<String> entries)
		{
			this.entries = entries;
			setPreferredSize(new Dimension(320, 320));
			setBackground(new Color(35, 35, 35));
		}

		private void setRotation(double rotation)
		{
			this.rotation = rotation;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int size = Math.min(getWidth(), getHeight()) - 38;
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2 + 8;
			double slice = 360.0 / entries.size();
			g.rotate(Math.toRadians(rotation), x + size / 2.0, y + size / 2.0);
			for (int i = 0; i < entries.size(); i++)
			{
				g.setColor(i % 2 == 0 ? new Color(190, 120, 30) : new Color(45, 100, 145));
				Arc2D sector = new Arc2D.Double(x, y, size, size, -90 + i * slice, slice, Arc2D.PIE);
				g.fill(sector);
				g.setColor(Color.WHITE);
				g.setStroke(new BasicStroke(2));
				g.draw(sector);
				if (entries.size() <= 12)
				{
					double angle = Math.toRadians(-90 + (i + 0.5) * slice);
					int tx = x + size / 2 + (int) (Math.cos(angle) * size * 0.30) - 30;
					int ty = y + size / 2 + (int) (Math.sin(angle) * size * 0.30);
					g.drawString(shortName(entries.get(i)), tx, ty);
				}
			}
			g.dispose();
			Graphics2D pointer = (Graphics2D) graphics.create();
			pointer.setColor(Color.WHITE);
			pointer.fillPolygon(new int[]{getWidth()/2-10, getWidth()/2+10, getWidth()/2}, new int[]{8,8,28}, 3);
			pointer.dispose();
		}

		private String shortName(String name)
		{
			return name.length() > 17 ? name.substring(0, 15) + "…" : name;
		}
	}

	private WheelSpinDialog() {}
}
