package edu.umd.marbl.mhap.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.util.OpenBitSet;

import edu.umd.marbl.mhap.utils.MhapRuntimeException;
import edu.umd.marbl.mhap.utils.Utils;

public class ValidBitVectorsFileBuilder
{

	public OpenBitSet readValidBitVectorsFile(String validBitVectorsName)
	{
		ObjectInputStream inputStream = null;
		OpenBitSet validKmersHashes = null;
		try
		{
			System.err.println("Reading binary file of valid kmer hashes");

			inputStream = new ObjectInputStream(new FileInputStream(validBitVectorsName));
			long[] validBitVector = (long[]) inputStream.readObject();

			validKmersHashes = new OpenBitSet(validBitVector, validBitVector.length);

			inputStream.close();
		} catch (Exception e)
		{
			System.err.println("There was a problem opening the file: " + e);
		}

		return validKmersHashes;
	}

	public void createValidBitVectorsFile(String validBitVectorsName, OpenBitSet validKmersHashes)
	{

		long startTime = System.nanoTime();
		try
		{
			ObjectOutputStream outputStream = null;
			outputStream = new ObjectOutputStream(new FileOutputStream(validBitVectorsName));

			System.err.println("Creating binary file of valid kmer hashes");

			outputStream.writeObject(validKmersHashes.getBits());

			outputStream.close();
		} catch (Exception e)
		{
			throw new MhapRuntimeException("Could not parse valid k-mer file.", e);
		}

		System.err.println("Time (s) to read valid kmer file: " + (System.nanoTime() - startTime) * 1.0e-9);
	}

	public void createKmersFileFromBinaryBitVectors(String validKmersFile, String validBitVectorsFileName, int kmerSize) throws IOException
	{
		FileWriter writer = new FileWriter("kmersFromBinaryFile.kmer");

		OpenBitSet validKmersHashes = null;
		OpenBitSet validKmersHashesGenerated = null;
		List<String> storedKmers = new ArrayList<String>();

		long numBits = 2 * (long) Integer.MAX_VALUE + 1;

		System.err.println("Creating valid kmer hashes.");
		Utils.createValidKmerFilter(validKmersFile, kmerSize, 0);
		validKmersHashes = Utils.getValidKmerHashes();
		createValidBitVectorsFile(validBitVectorsFileName, validKmersHashes);

		validKmersHashesGenerated = readValidBitVectorsFile(validBitVectorsFileName);

		System.err.println("Creating reverse hash computation");
		for (long i = 0; i < numBits; i++)
		{
			if (validKmersHashesGenerated.get(i))
				storedKmers.add(Utils.reverseComputeHashYGS(i));
		}

		System.err.println("Writing results to new kmers file.");
		for (String kmer : storedKmers)
		{
			writer.write(kmer);
			writer.write("\n");
		}

		writer.close();
	}
}
