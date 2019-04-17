package org.eclipse.vtp.desktop.export.internal.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public abstract class ExportWriter implements Closeable, Flushable {

	public static ExportWriter create(File file) throws IOException {
		if (file.isDirectory())
			return new DirectoryWriter(file);
		else
			return new ArchiveWriter(file);
	}

	private final byte[] buffer = new byte[1024 * 10];

	public abstract OutputStream write(String entryName) throws IOException;

	public abstract void writeStream(String entryName, InputStream stream)
			throws IOException;

	public abstract void writeURL(String entryName, URL url) throws IOException;

	public abstract void writeFile(String entryName, File file, FilenameFilter filter)
			throws IOException;

	protected void copy(InputStream input, OutputStream output)
			throws IOException {
		for (int i = input.read(buffer); i >= 0; i = input.read(buffer))
			output.write(buffer, 0, i);
	}

	/** Implementation that writes to a directory. */
	private static final class DirectoryWriter extends ExportWriter {

		private File directory;

		public DirectoryWriter(File directory) throws IOException {
			this.directory = directory;
		}

		public OutputStream write(String entryName) throws IOException {
			File target = new File(directory, entryName).getCanonicalFile();
			File parent = target.getParentFile().getCanonicalFile();
			if (!parent.isDirectory() & !parent.mkdirs())
				throw new FileNotFoundException(parent.getAbsolutePath());
			return new BufferedOutputStream(new FileOutputStream(target));
		}

		public void writeStream(String entryName, InputStream stream)
				throws IOException {
			OutputStream output = write(entryName);
			try {
				copy(stream, output);
			} finally {
				output.close();
			}
		}

		public void writeURL(String entryName, URL url) throws IOException {
			OutputStream output = write(entryName);
			try {
				InputStream stream = url.openStream();
				try {
					copy(new BufferedInputStream(stream), output);
				} finally {
					stream.close();
				}
			} finally {
				output.close();
			}
		}

		public void writeFile(String entryName, File file, FilenameFilter filter) throws IOException {
			File canonical = file.getCanonicalFile();
			if (canonical.isFile()) {
				OutputStream output = write(entryName);
				try {
					FileInputStream stream = new FileInputStream(canonical);
					try {
						copy(new BufferedInputStream(stream), output);
					} finally {
						stream.close();
					}
				} finally {
					output.close();
				}
			} else {
				File[] children = filter == null ? canonical.listFiles() : canonical.listFiles(filter);
				if (children != null)
					for (File child : children)
						writeFile(
								entryName
										+ (child.isFile() ? child.getName()
												: child.getName() + "/"), child, filter);
			}
		}
		
		@Override
		public void flush() throws IOException {
			// Nothing to do.
		}
		
		@Override
		public void close() throws IOException {
			directory = null;
		}

	}

	/** Implementation that writes to a ZIP file. */
	private static final class ArchiveWriter extends ExportWriter {

		private final ZipOutputStream output;
		private final Set<String> writtenEntries = new HashSet<String>();

		public ArchiveWriter(File archive) throws IOException {
			FileOutputStream fileOutput = new FileOutputStream(archive);
			boolean failed = true;
			try {
				output = new ZipOutputStream(new BufferedOutputStream(
						fileOutput));
				failed = false;
			} finally {
				if (failed)
					fileOutput.close();
			}
		}

		public OutputStream write(String entryName) throws IOException {
			writeParentEntries(entryName);
			startEntry(entryName, true);
			return new OutputStream() {
				public void write(int b) throws IOException {
					output.write(b);
				}

				public void write(byte[] b) throws IOException {
					output.write(b);
				}

				public void write(byte[] b, int off, int len)
						throws IOException {
					output.write(b, off, len);
				}

				public void close() throws IOException {
					output.closeEntry();
				}
			};
		}

		public void writeStream(String entryName, InputStream stream)
				throws IOException {
			writeParentEntries(entryName);
			startEntry(entryName, true);
			try {
				copy(stream, output);
			} finally {
				output.closeEntry();
			}
		}

		public void writeURL(String entryName, URL url) throws IOException {
			writeParentEntries(entryName);
			startEntry(entryName, true);
			try {
				InputStream stream = url.openStream();
				try {
					copy(new BufferedInputStream(stream), output);
				} finally {
					stream.close();
				}
			} finally {
				output.closeEntry();
			}
		}

		public void writeFile(String entryName, File file, FilenameFilter filter) throws IOException {
			File canonical = file.getCanonicalFile();
			if (canonical.isFile()) {
				writeParentEntries(entryName);
				startEntry(entryName, true);
				try {
					FileInputStream stream = new FileInputStream(canonical);
					try {
						copy(new BufferedInputStream(stream), output);
					} finally {
						stream.close();
					}
				} finally {
					output.closeEntry();
				}
			} else {
				File[] children = filter == null ? canonical.listFiles() : canonical.listFiles(filter);
				if (children != null)
					for (File child : children)
						writeFile(
								entryName
										+ (child.isFile() ? child.getName()
												: child.getName() + "/"), child, filter);
			}
		}

		public void flush() throws IOException {
			output.flush();
		}

		public void close() throws IOException {
			output.close();
		}

		private void writeParentEntries(String entryName) throws IOException {
			int lastSlash = entryName.lastIndexOf('/');
			if (lastSlash == entryName.length() - 1)
				lastSlash = entryName.lastIndexOf('/', lastSlash - 1);
			if (lastSlash < 0)
				return;
			String parentEntryName = entryName.substring(0, lastSlash + 1);
			writeParentEntries(parentEntryName);
			if (startEntry(parentEntryName, false))
				output.closeEntry();
		}

		private boolean startEntry(String entryName, boolean failOnDuplicate)
				throws IOException {
			writeParentEntries(entryName);
			if (!writtenEntries.add(entryName)) {
				if (failOnDuplicate)
					throw new ZipException(String.format(
							"An entry with the name \"%s\" already exisits.",
							entryName));
				else
					return false;
			}
			output.putNextEntry(new ZipEntry(entryName));
			return true;
		}

	}

}
