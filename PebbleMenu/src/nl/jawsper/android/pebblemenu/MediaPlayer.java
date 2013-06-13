package nl.jawsper.android.pebblemenu;

public class MediaPlayer
{
	private String m_Name;
	private String m_PackageName;
	private String m_ClassName;

	public MediaPlayer( String a_Name, String a_PackageName, String a_ClassName )
	{
		m_Name = a_Name;
		m_PackageName = a_PackageName;
		m_ClassName = a_ClassName;
	}

	public String getName()
	{
		return m_Name;
	}

	public void setName( String a_Name )
	{
		this.m_Name = a_Name;
	}

	public String getPackageName()
	{
		return m_PackageName;
	}

	public void setPackageName( String a_PackageName )
	{
		this.m_PackageName = a_PackageName;
	}

	public String getClassName()
	{
		return m_ClassName;
	}

	public void setClassName( String a_ClassName )
	{
		this.m_ClassName = a_ClassName;
	}

	@Override public String toString()
	{
		return m_Name;
	}
	
	@Override public boolean equals( Object o )
	{
		if( o instanceof MediaPlayer )
		{
			MediaPlayer other = (MediaPlayer)o;
			return m_PackageName == other.m_PackageName && m_ClassName == other.m_ClassName;
		}
		return super.equals( o );
	}
}