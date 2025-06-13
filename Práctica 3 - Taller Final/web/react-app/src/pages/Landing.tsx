import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';

const Landing = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const contentRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleScroll = () => {
      if (!containerRef.current || !contentRef.current) return;
      
      const scrolled = window.scrollY;
      const viewportHeight = window.innerHeight;
      const totalHeight = containerRef.current.scrollHeight;
      
      // Calculate scale based on scroll position
      const scale = 1 + (scrolled / (totalHeight - viewportHeight)) * 0.3;
      const opacity = 1 - (scrolled / (totalHeight - viewportHeight));
      
      contentRef.current.style.transform = `scale(${scale})`;
      contentRef.current.style.opacity = opacity.toString();
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <div 
      ref={containerRef} 
      className="relative min-h-[200vh] bg-black overflow-hidden"
      style={{ scrollbarWidth: 'none' }}
    >
      {/* Hide scrollbar for Chrome, Safari and Opera */}
      <style jsx global>{`
        html::-webkit-scrollbar,
        body::-webkit-scrollbar {
            display: none;
        }
        
        html,
        body {
            -ms-overflow-style: none;  /* IE and Edge */
            scrollbar-width: none;  /* Firefox */
        }
        
        * {
            scrollbar-width: none;
            -ms-overflow-style: none;
        }
        
        *::-webkit-scrollbar {
            display: none;
        }

        @keyframes twinkle {
          0%, 100% {
            opacity: 0.2;
            filter: blur(1px);
          }
          50% {
            opacity: 1;
            filter: blur(0.5px);
          }
        }

        @keyframes float {
          0%, 100% {
            transform: translateY(0);
          }
          50% {
            transform: translateY(-10px);
          }
        }
        `}
      </style>
      
      {/* Background*/}
      <div className="fixed inset-0">
        {/* Gradient */}
        <div className="absolute inset-0 bg-gradient-to-br from-black via-[#0a1f13] to-black" />
        
        {/* Stars */}
        {Array.from({ length: 100 }, (_, i) => (
          <div
            key={i}
            className="absolute"
            style={{
              top: `${Math.random() * 100}%`,
              left: `${Math.random() * 100}%`,
              width: `${Math.random() * 3 + 1}px`,
              height: `${Math.random() * 3 + 1}px`,
              background: 'radial-gradient(circle at center, #1db954, #25d366 30%, transparent 70%)',              
              animation: `twinkle ${2 + Math.random() * 3}s ease-in-out infinite`,
              animationDelay: `${Math.random() * 2}s`,
              opacity: Math.random() * 0.7 + 0.3,
              boxShadow: '0 0 15px 4px rgba(29, 185, 84, 0.7)',
              borderRadius: '50%',
              filter: 'blur(0.5px)'
            }}
          />
        ))}

        {/* Larger, brighter stars with more pronounced glow */}
        {Array.from({ length: 30 }, (_, i) => (
          <div
            key={`bright-${i}`}
            className="absolute"
            style={{
              top: `${Math.random() * 100}%`,
              left: `${Math.random() * 100}%`,
              width: `${Math.random() * 4 + 2}px`,
              height: `${Math.random() * 4 + 2}px`,
              background: 'radial-gradient(circle at center, #1db954, #25d366 40%, transparent 80%)',
              animation: `twinkle ${2 + Math.random() * 3}s ease-in-out infinite`,
              animationDelay: `${Math.random() * 2}s`,
              opacity: Math.random() * 0.3 + 0.7,
              boxShadow: '0 0 20px 6px rgba(29, 185, 84, 0.8)',
              borderRadius: '50%',
              filter: 'blur(1px)'
            }}
          />
        ))}
      </div>

      {/* Overlay gradient */}
      <div className="fixed inset-0 bg-gradient-to-b from-black/80 via-black/60 to-black/80 pointer-events-none" />

      {/* Initial content that scales */}
      <div
        ref={contentRef}
        className="fixed inset-0 flex items-center justify-center"
      >
        <div className="text-center z-10 px-4">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 1.5, delay: 0.5, ease: "easeOut" }}
            className="relative inline-block"
          >
            <h1 className="text-8xl md:text-9xl font-black text-transparent bg-clip-text bg-gradient-to-b from-[#1db954] via-[#25d366] to-[#2ecc71] tracking-tighter mb-8 font-serif"
                style={{ fontFamily: "'Playfair Display', serif" }}>
              KINORA
            </h1>
            <div className="absolute -inset-4 blur-3xl bg-gradient-to-br from-[#1db954]/25 via-[#25d366]/15 to-[#2ecc71]/10 -z-10" />
          </motion.div>
          
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 1.2, delay: 1.0, ease: "easeOut" }}
            className="text-xl md:text-2xl text-[#c8ffdb] mb-12 max-w-3xl mx-auto font-light leading-relaxed"
            style={{ fontFamily: "'Cormorant Garamond', serif" }}
          >
            Descubre un universo de historias infinitas. 
            <span className="block mt-2 text-[#abedc2]">
              Tu próxima aventura te espera...
            </span>
          </motion.p>
        </div>
      </div>

      {/* Fixed bottom content */}
      <div className="fixed bottom-0 left-0 right-0 bg-gradient-to-t from-black via-black/80 to-transparent h-96 flex items-end justify-center pb-16">
        <div className="text-center space-y-8">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 1.0, delay: 1.5, ease: "easeOut" }}
            className="space-x-6"
          >
            <Link
              to="/login"
              className="inline-block bg-[#1db954] text-black font-bold py-4 px-10 rounded-full text-lg tracking-wide transition-all duration-300 hover:scale-105 hover:shadow-[0_0_30px_rgba(29,185,84,0.5)] transform-gpu"
              style={{ fontFamily: "'Montserrat', sans-serif" }}
            >
              Iniciar sesión
            </Link>
            <Link
              to="/register"
              className="inline-block bg-transparent text-[#1db954] font-bold py-4 px-10 rounded-full text-lg tracking-wide border-2 border-[#1db954]/80 transition-all duration-300 hover:scale-105 hover:border-[#1db954] hover:shadow-[0_0_30px_rgba(29,185,84,0.3)] transform-gpu"
              style={{ fontFamily: "'Montserrat', sans-serif" }}
            >
              Registrarse
            </Link>
          </motion.div>
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 1.0, delay: 2.0, ease: "easeOut" }}
            className="text-[#abedc2]/80 text-base font-light"
            style={{ fontFamily: "'Cormorant Garamond', serif" }}
          >
            ¡Únete a nuestra comunidad de amantes del cine!
          </motion.p>
        </div>
      </div>
    </div>
  );
};

export default Landing;