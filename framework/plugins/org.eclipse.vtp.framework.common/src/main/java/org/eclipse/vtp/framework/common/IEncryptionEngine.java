package org.eclipse.vtp.framework.common;

/**
 * A simple interface for encrypting process configuration data.
 * 
 * @author Lonnie Pryor
 */
public interface IEncryptionEngine
{
	/**
	 * Encrypts the supplied bytes using an implementation-specific strategy.
	 * 
	 * @param plainBytes The bytes to be encrypted.
	 * @return The encrypted data.
	 * @throws NullPointerException If <code>plainBytes</code> is
	 *           <code>null</code>.
	 */
	byte[] encrypt(byte[] plainBytes) throws NullPointerException;

	/**
	 * Decrypts the supplied bytes using an implementation-specific strategy.
	 * 
	 * @param cypherBytes The bytes to be decrypted.
	 * @return The decrypted data.
	 * @throws NullPointerException If <code>cypherBytes</code> is
	 *           <code>null</code>.
	 */
	byte[] decrypt(byte[] cypherBytes) throws NullPointerException;

	/**
	 * Encrypts the supplied text using an implementation-specific strategy.
	 * 
	 * @param plainText The text to be encrypted.
	 * @return The encrypted text.
	 * @throws NullPointerException If <code>plainText</code> is
	 *           <code>null</code>.
	 */
	char[] encrypt(char[] plainText) throws NullPointerException;

	/**
	 * Decrypts the supplied text using an implementation-specific strategy.
	 * 
	 * @param cypherText The text to be decrypted.
	 * @return The decrypted text.
	 * @throws NullPointerException If <code>cypherText</code> is
	 *           <code>null</code>.
	 */
	char[] decrypt(char[] cypherText) throws NullPointerException;
}
