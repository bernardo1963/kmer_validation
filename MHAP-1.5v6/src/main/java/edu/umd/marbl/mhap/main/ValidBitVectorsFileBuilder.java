package edu.umd.marbl.mhap.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
		}catch(Exception e){
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
		}
		catch (Exception e)
		{
			throw new MhapRuntimeException("Could not parse valid k-mer file.", e);
		}
		
		System.err.println("Time (s) to read valid kmer file: " + (System.nanoTime() - startTime) * 1.0e-9);
	}
}
