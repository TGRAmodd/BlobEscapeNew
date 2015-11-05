#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;

uniform float u_usesDiffuseTexture;

uniform vec4 u_globalAmbient;

uniform vec4 u_lightColor;
uniform vec4 u_lightColor2;
uniform vec4 u_lightColor3;
uniform vec4 u_spotDirection;
uniform float u_spotExponent;

uniform float u_constantAttenuation;
uniform float u_linearAttenuation;
uniform float u_quadraticAttenuation;

uniform vec4 u_materialDiffuse;
uniform vec4 u_materialSpecular;
uniform float u_materialShininess;

uniform vec4 u_materialEmission;

varying vec2 v_uv;
varying vec4 v_normal;
varying vec4 v_s;
varying vec4 v_s2;
varying vec4 v_s3;
varying vec4 v_h;
varying vec4 v_h2;
varying vec4 v_h3;

void main()
{
	vec4 materialDiffuse;
	if(u_usesDiffuseTexture == 1.0)
	{
		materialDiffuse = texture2D(u_diffuseTexture, v_uv);  //also * u_materialDiffuse ??? up to you.
	}
	else
	{
		materialDiffuse = u_materialDiffuse;
	}

	vec4 materialSpecular = u_materialSpecular;

	//Lighting
	
	
	//Light1 beginning
	float length_s = length(v_s);
	
	float lambert = max(0.0, dot(v_normal, v_s) / (length(v_normal) * length_s));
	float phong = max(0.0, dot(v_normal, v_h) / (length(v_normal) * length(v_h)));

	vec4 diffuseColor = lambert * u_lightColor * materialDiffuse;

	vec4 specularColor = pow(phong, u_materialShininess) * u_lightColor * materialSpecular;

	float attenuation = 1.0;
	if(u_spotExponent != 0.0)
	{
		float spotAttenuation = max(0.0, dot(-v_s, u_spotDirection) / (length_s * length(u_spotDirection)));
		spotAttenuation = pow(spotAttenuation, u_spotExponent);
		attenuation *= spotAttenuation;
	}
	attenuation *= 1.0 / (u_constantAttenuation + length_s * u_linearAttenuation + pow(length_s, 2.0) * u_quadraticAttenuation);
		
	vec4 light1CalcColor = attenuation * (diffuseColor + specularColor);
	//Light1 end
	
	
	
	//Light2 beginning
	length_s = length(v_s2);
	
	lambert = max(0.0, dot(v_normal, v_s2) / (length(v_normal) * length_s));
	phong = max(0.0, dot(v_normal, v_h2) / (length(v_normal) * length(v_h2)));

	diffuseColor = lambert * u_lightColor2 * materialDiffuse;

	specularColor = pow(phong, u_materialShininess) * u_lightColor2 * materialSpecular;

	attenuation *= 1.0 / (u_constantAttenuation + length_s * u_linearAttenuation + pow(length_s, 2.0) * u_quadraticAttenuation);
		
	vec4 light2CalcColor = attenuation * (diffuseColor + specularColor);
	//Light2 end
	
	
	
	//Light3 beginning
	length_s = length(v_s3);
	
	lambert = max(0.0, dot(v_normal, v_s3) / (length(v_normal) * length_s));
	phong = max(0.0, dot(v_normal, v_h3) / (length(v_normal) * length(v_h3)));

	diffuseColor = lambert * u_lightColor3 * materialDiffuse;

	specularColor = pow(phong, u_materialShininess) * u_lightColor3 * materialSpecular;

	attenuation *= 1.0 / (u_constantAttenuation + length_s * u_linearAttenuation + pow(length_s, 2.0) * u_quadraticAttenuation);
		
	vec4 light3CalcColor = attenuation * (diffuseColor + specularColor);
	//Light3 end
	

	//gl_FragColor = u_globalAmbient + u_materialEmission + light1CalcColor;
	gl_FragColor = u_globalAmbient * materialDiffuse + u_materialEmission + light1CalcColor + light2CalcColor + light3CalcColor;
	//gl_FragColor = u_globalAmbient * materialDiffuse + u_materialEmission + light1CalcColor + light2CalcColor;
	//gl_FragColor.a = 0.5;
}