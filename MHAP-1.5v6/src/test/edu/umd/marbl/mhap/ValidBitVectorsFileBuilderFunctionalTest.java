package edu.umd.marbl.mhap;

import static org.testng.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.util.OpenBitSet;
import org.testng.annotations.Test;

import edu.umd.marbl.mhap.main.ValidBitVectorsFileBuilder;
import edu.umd.marbl.mhap.utils.Utils;

public class ValidBitVectorsFileBuilderFunctionalTest 
{
	final private String validKmersFile = "/tmp/testKmerFile.kmer";
	final private String validBitVectorsFileName = "testFile.bitVector";
	
	@Test
	public void test_CreateBitVectorFile_ShouldCreateBitVectorsFile() throws IOException
	{
		ValidBitVectorsFileBuilder bitVectorsBuilder = new ValidBitVectorsFileBuilder();
		OpenBitSet validKmersHashes;
		
		File bitVectorFile = new File(validBitVectorsFileName);
		assertFalse(bitVectorFile.exists());
		
		// sempre colocar o arquivo de teste no temp antes de rodar
		Utils.createValidKmerFilter(validKmersFile, 16, 0);
		validKmersHashes = Utils.getValidKmerHashes();
		
		bitVectorsBuilder.createValidBitVectorsFile(validBitVectorsFileName, validKmersHashes);
		
		assertTrue(bitVectorFile.exists());
	}
	
	@Test
	public void test_ReadBitVectorFile_ShouldReadBitVectorsFileToOpenBitSet() throws IOException
	{
		ValidBitVectorsFileBuilder bitVectorsBuilder = new ValidBitVectorsFileBuilder();
		
		OpenBitSet validKmersHashesOriginal = null;
		OpenBitSet validKmersHashesGenerated = null;
		
		Utils.createValidKmerFilter(validKmersFile, 16, 0);
		validKmersHashesOriginal = Utils.getValidKmerHashes();
		bitVectorsBuilder.createValidBitVectorsFile(validBitVectorsFileName, validKmersHashesOriginal);
		
		File bitVectorFile = new File(validBitVectorsFileName);
		assertTrue(bitVectorFile.exists());
		
		validKmersHashesGenerated = bitVectorsBuilder.readValidBitVectorsFile(validBitVectorsFileName);
		
		assertNotNull(validKmersHashesGenerated);
		assertEquals(validKmersHashesGenerated, validKmersHashesOriginal);
		
		
		BufferedReader bf = new BufferedReader(new FileReader(validKmersFile), 8388608);
		String line = bf.readLine();
		
		//int count = 0;
		while(line != null)
		{
			long kmerHash = Utils.computeHashYGS(line);
			assertTrue(validKmersHashesOriginal.fastGet(kmerHash));
			assertTrue(validKmersHashesGenerated.get(kmerHash));
			
			kmerHash = Utils.computeHashYGS(Utils.rc(line));
			assertTrue(validKmersHashesOriginal.fastGet(kmerHash));
			assertTrue(validKmersHashesGenerated.get(kmerHash));
			
			line = bf.readLine();
		}
	}
}
