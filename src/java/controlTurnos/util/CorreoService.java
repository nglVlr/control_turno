package controlTurnos.util;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CorreoService {

    // Cambiar por el correo y contraseña de aplicación real
    // Para obtener la contraseña de aplicación:
    // Google Account → Seguridad → Verificación en 2 pasos → Contraseñas de aplicaciones
    private static final String CORREO_ORIGEN   = "angelmayen11@gmail.com";
    private static final String CONTRASENA_APP  = "lufa xvbh apiv cavc";
    private static final String NOMBRE_SISTEMA  = AppConfig.NOMBRE_SISTEMA;

    public static void enviarBienvenida(String correoDestino, String nombreCompleto,
                                        String usuario, String contrasena,
                                        String nombreRol, String nombreArea,
                                        String nombreTurno) {
        // Si no está configurado el correo, saltar silenciosamente
        if (CORREO_ORIGEN.contains("tucorreo") || CONTRASENA_APP.contains("xxxx")) {
            System.out.println("CorreoService: credenciales no configuradas, se omite el envío.");
            return;
        }
        try {
            System.out.println("CorreoService: intentando enviar a " + correoDestino);
            System.out.println("CorreoService: desde " + CORREO_ORIGEN);
            Session session = crearSesion();
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(CORREO_ORIGEN, NOMBRE_SISTEMA));
            mensaje.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(correoDestino));
            mensaje.setSubject("Bienvenido al Sistema Control de Turnos");
            mensaje.setContent(construirHtml(nombreCompleto, usuario, contrasena,
                    nombreRol, nombreArea, nombreTurno), "text/html; charset=UTF-8");
            Transport.send(mensaje);
            System.out.println("CorreoService: correo enviado exitosamente a " + correoDestino);
        } catch (Exception e) {
            System.out.println("CorreoService ERROR: " + e.getClass().getName());
            System.out.println("CorreoService ERROR mensaje: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("CorreoService ERROR causa: " + e.getCause().getMessage());
            }
        }
    }

    private static Session crearSesion() {
        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");
        props.put("mail.smtp.ssl.trust",       "smtp.gmail.com");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CORREO_ORIGEN, CONTRASENA_APP);
            }
        });
    }

    private static String construirHtml(String nombre, String usuario,
            String contrasena, String rol, String area, String turno) {
        String infoAdicional = "";
        if (area != null && !area.isEmpty() && !"null".equals(area)) {
            infoAdicional += "<tr><td style='padding:8px 0;color:#64748b;'>Área</td>"
                           + "<td style='padding:8px 0;font-weight:600;'>" + area + "</td></tr>";
        }
        if (turno != null && !turno.isEmpty() && !"null".equals(turno)) {
            infoAdicional += "<tr><td style='padding:8px 0;color:#64748b;'>Turno</td>"
                           + "<td style='padding:8px 0;font-weight:600;'>" + turno + "</td></tr>";
        }

        return "<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'></head><body "
             + "style='margin:0;padding:0;background:#f0f4f8;font-family:Segoe UI,sans-serif;'>"
             + "<table width='100%' style='background:#f0f4f8;padding:40px 0;'><tr><td align='center'>"
             + "<table width='600' style='background:#ffffff;border-radius:16px;"
             + "box-shadow:0 4px 24px rgba(0,0,0,0.08);overflow:hidden;'>"

             + "<tr><td style='background:linear-gradient(135deg,#2C3E50,#3498DB);"
             + "padding:40px 40px 30px;text-align:center;'>"
             + "<div style='width:64px;height:64px;background:rgba(255,255,255,0.2);"
             + "border-radius:50%;margin:0 auto 16px;display:flex;align-items:center;"
             + "justify-content:center;font-size:28px;'>⏰</div>"
             + "<h1 style='color:#ffffff;margin:0;font-size:26px;font-weight:700;'>"
             + "Control de Turnos</h1>"
             + "<p style='color:rgba(255,255,255,0.8);margin:8px 0 0;font-size:14px;'>"
             + AppConfig.VERSION + "</p>"
             + "</td></tr>"

             + "<tr><td style='padding:40px 40px 24px;'>"
             + "<h2 style='color:#2C3E50;margin:0 0 8px;font-size:22px;'>"
             + "¡Bienvenido, " + nombre + "! 🎉</h2>"
             + "<p style='color:#64748b;margin:0;font-size:15px;line-height:1.6;'>"
             + "Tu cuenta ha sido creada exitosamente en el Sistema de Control de Turnos. "
             + "A continuación encontrarás tus credenciales de acceso.</p>"
             + "</td></tr>"

             + "<tr><td style='padding:0 40px 24px;'>"
             + "<div style='background:#f8fafc;border-radius:12px;padding:24px;"
             + "border-left:4px solid #3498DB;'>"
             + "<h3 style='color:#2C3E50;margin:0 0 16px;font-size:16px;'>"
             + "🔑 Credenciales de Acceso</h3>"
             + "<table width='100%' style='border-collapse:collapse;'>"
             + "<tr><td style='padding:8px 0;color:#64748b;width:120px;'>Usuario</td>"
             + "<td style='padding:8px 0;'>"
             + "<span style='background:#2C3E50;color:#ffffff;padding:4px 12px;"
             + "border-radius:6px;font-family:monospace;font-size:15px;font-weight:600;'>"
             + usuario + "</span></td></tr>"
             + "<tr><td style='padding:8px 0;color:#64748b;'>Contraseña</td>"
             + "<td style='padding:8px 0;'>"
             + "<span style='background:#3498DB;color:#ffffff;padding:4px 12px;"
             + "border-radius:6px;font-family:monospace;font-size:15px;font-weight:600;'>"
             + contrasena + "</span></td></tr>"
             + "<tr><td style='padding:8px 0;color:#64748b;'>Rol</td>"
             + "<td style='padding:8px 0;font-weight:600;color:#2C3E50;'>" + rol + "</td></tr>"
             + infoAdicional
             + "</table></div></td></tr>"

             + "<tr><td style='padding:0 40px 24px;'>"
             + "<div style='background:#fff3cd;border-radius:10px;padding:16px;"
             + "border-left:4px solid #f59e0b;'>"
             + "<p style='color:#92400e;margin:0;font-size:13px;line-height:1.6;'>"
             + "⚠️ <strong>Por seguridad</strong>, te recomendamos cambiar tu contraseña "
             + "la primera vez que inicies sesión. Mantén tus credenciales confidenciales."
             + "</p></div></td></tr>"

             + "<tr><td style='padding:0 40px 32px;text-align:center;'>"
             + "<p style='color:#64748b;margin:0 0 16px;font-size:14px;'>"
             + "Puedes acceder al sistema desde:</p>"
             + "<div style='background:#f0f4f8;border-radius:8px;padding:12px 20px;"
             + "display:inline-block;'>"
             + "<span style='color:#3498DB;font-family:monospace;font-size:14px;'>"
             + "http://localhost:8080/ControlTurnos</span></div>"
             + "</td></tr>"

             + "<tr><td style='background:#f8fafc;padding:24px 40px;text-align:center;"
             + "border-top:1px solid #e2e8f0;'>"
             + "<p style='color:#94a3b8;margin:0;font-size:12px;'>"
             + "Este correo fue generado automáticamente por el Sistema Control de Turnos. "
             + "No respondas a este mensaje.</p>"
             + "</td></tr>"

             + "</table></td></tr></table>"
             + "</body></html>";
    }
}
