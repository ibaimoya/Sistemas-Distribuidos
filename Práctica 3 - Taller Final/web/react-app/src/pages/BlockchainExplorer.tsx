import React, { useState, useEffect } from 'react';
import { ArrowLeft, Shield, Hash, Clock, Link as LinkIcon, CheckCircle, XCircle, RefreshCw, X, AlertTriangle, Copy, Check } from 'lucide-react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';

interface Block {
  index:        number;
  timestamp:    number;
  data:         string;
  hash:         string;
  previousHash: string;
  nonce:        number;
}

interface BlockchainInfo {
  totalBlocks:     number;
  difficulty:      number;
  isValid:         boolean;
  latestBlockHash: string;
}

export default function BlockchainExplorer() {
  const [blocks, setBlocks] = useState<Block[]>([]);
  const [blockchainInfo, setBlockchainInfo] = useState<BlockchainInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedBlock, setSelectedBlock] = useState<Block | null>(null);
  const [validating, setValidating] = useState(false);
  const [validationResult, setValidationResult] = useState<{ valid: boolean; message: string } | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [showJson, setShowJson] = useState(false);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    checkAuth();
    loadBlockchainData();
  }, []);

  const checkAuth = async () => {
    try {
      const response = await fetch('/auth/check', { credentials: 'include' });
      const data = await response.json();
      setIsAdmin(data.admin || false);
    } catch (error) {
      console.error('Error checking auth:', error);
    }
  };

  const loadBlockchainData = async () => {
    setLoading(true);
    try {
      const [infoRes, blocksRes] = await Promise.all([
        fetch('/api/blockchain/info', { credentials: 'include' }),
        fetch('/api/blockchain/blocks', { credentials: 'include' })
      ]);

      if (!infoRes.ok || !blocksRes.ok) {
        console.error('Error loading blockchain data');
        return;
      }

      const info = await infoRes.json();
      const blocksData = await blocksRes.json();

      console.log('Blockchain info loaded:', info); // Debug log

      setBlockchainInfo(info);
      setBlocks(blocksData);
    } catch (error) {
      console.error('Error loading blockchain data:', error);
    } finally {
      setLoading(false);
    }
  };

  const validateBlockchain = async () => {
    setValidating(true);
    setValidationResult(null);
    
    try {
      const response = await fetch('/api/blockchain/validate', { credentials: 'include' });
      const result = await response.json();
      setValidationResult(result);
      
      // Recargar información después de validar
      await loadBlockchainData();
    } catch (error) {
      console.error('Error validating blockchain:', error);
      setValidationResult({ valid: false, message: 'Error al validar' });
    } finally {
      setValidating(false);
    }
  };

  const resetBlockchain = async () => {
    if (!confirm('¿Estás seguro de que quieres reiniciar la blockchain? Esto eliminará todos los bloques excepto el génesis.')) {
      return;
    }
    
    try {
      const response = await fetch('/api/blockchain/reset', { 
        method: 'POST',
        credentials: 'include' 
      });
      
      if (response.ok) {
        setValidationResult({ valid: true, message: 'Blockchain reiniciada exitosamente' });
        await loadBlockchainData();
      }
    } catch (error) {
      console.error('Error resetting blockchain:', error);
      setValidationResult({ valid: false, message: 'Error al reiniciar la blockchain' });
    }
  };

  const copyToClipboard = async () => {
    if (!selectedBlock) return;
    
    try {
      await navigator.clipboard.writeText(JSON.stringify(selectedBlock, null, 2));
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (error) {
      console.error('Error copying to clipboard:', error);
    }
  };

  const parseBlockData = (data: string) => {
    if (data === 'Genesis Block') return { type: 'genesis', content: 'Bloque Génesis' };
    
    const parts = data.split('|');
    const parsed: any = {};
    
    parts.forEach(part => {
      const [key, value] = part.split(':');
      if (key && value) {
        parsed[key] = value;
      }
    });
    
    return {
      type: 'rating',
      userId: parsed.USER || 'N/A',
      movieId: parsed.MOVIE || 'N/A',
      rating: parsed.RATING || 'N/A',
      timestamp: parsed.TIME || 'N/A'
    };
  };

  const formatTimestamp = (timestamp: number) => {
    return new Date(timestamp).toLocaleString('es-ES', {
      dateStyle: 'short',
      timeStyle: 'medium'
    });
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#111111] flex justify-center items-center">
        <div className="flex space-x-2">
          <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce"></div>
          <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
          <div className="w-3 h-3 bg-[#1db954] rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
        </div>
      </div>
    );
  }

  // Determinar si la blockchain es válida basándose en el estado actual
  const isBlockchainValid = blockchainInfo?.isValid ?? false;

  return (
    <div className="min-h-screen bg-[#111111] text-white">
      {/* Header */}
      <header className="fixed w-full z-50 flex justify-between items-center px-8 py-4 bg-gradient-to-b from-black/80 to-transparent">
        <div className="flex items-center space-x-4">
          <Link to="/" className="text-[#1db954] hover:text-[#1ed760] transition-colors">
            <ArrowLeft size={24} />
          </Link>
          <h1 className="text-3xl font-bold text-[#1db954]">Explorador de Blockchain</h1>
        </div>
      </header>

      <main className="pt-24 px-8 pb-8">
        <div className="max-w-7xl mx-auto">
          {/* Blockchain Info Card */}
          {blockchainInfo && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-[#1a1a1a] rounded-xl p-6 mb-8"
            >
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-2xl font-bold flex items-center space-x-2">
                  <Shield className="text-[#1db954]" />
                  <span>Estado de la Blockchain</span>
                </h2>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={validateBlockchain}
                    disabled={validating}
                    className="flex items-center space-x-2 px-4 py-2 bg-[#1db954]/10 text-[#1db954] rounded-lg hover:bg-[#1db954]/20 transition-colors disabled:opacity-50"
                  >
                    <RefreshCw size={16} className={validating ? 'animate-spin' : ''} />
                    <span>Validar Integridad</span>
                  </button>
                  {isAdmin && !isBlockchainValid && (
                    <button
                      onClick={resetBlockchain}
                      className="flex items-center space-x-2 px-4 py-2 bg-red-500/10 text-red-500 rounded-lg hover:bg-red-500/20 transition-colors"
                    >
                      <AlertTriangle size={16} />
                      <span>Reiniciar</span>
                    </button>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-black/20 rounded-lg p-4">
                  <p className="text-gray-400 text-sm mb-1">Total de Bloques</p>
                  <p className="text-2xl font-bold text-[#1db954]">{blockchainInfo.totalBlocks}</p>
                </div>
                <div className="bg-black/20 rounded-lg p-4">
                  <p className="text-gray-400 text-sm mb-1">Dificultad</p>
                  <p className="text-2xl font-bold">{blockchainInfo.difficulty}</p>
                </div>
                <div className="bg-black/20 rounded-lg p-4">
                  <p className="text-gray-400 text-sm mb-1">Estado</p>
                  <div className="flex items-center space-x-2">
                    {isBlockchainValid ? (
                      <>
                        <CheckCircle className="text-[#1db954]" size={20} />
                        <span className="text-[#1db954] font-semibold">Válida</span>
                      </>
                    ) : (
                      <>
                        <XCircle className="text-red-500" size={20} />
                        <span className="text-red-500 font-semibold">Comprometida</span>
                      </>
                    )}
                  </div>
                </div>
              </div>

              {/* Validation Result */}
              <AnimatePresence>
                {validationResult && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    exit={{ opacity: 0, height: 0 }}
                    className={`mt-4 p-4 rounded-lg ${
                      validationResult.valid ? 'bg-[#1db954]/10 border border-[#1db954]/20' : 'bg-red-500/10 border border-red-500/20'
                    }`}
                  >
                    <p className={validationResult.valid ? 'text-[#1db954]' : 'text-red-400'}>
                      {validationResult.message}
                    </p>
                  </motion.div>
                )}
              </AnimatePresence>
            </motion.div>
          )}

          {/* Blocks List */}
          <div className="space-y-4">
            <h2 className="text-2xl font-bold mb-4">Cadena de Bloques</h2>
            
            {blocks.map((block, index) => {
              const blockData = parseBlockData(block.data);
              const isGenesis = blockData.type === 'genesis';
              
              return (
                <motion.div
                  key={block.index}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                  className="bg-[#1a1a1a] rounded-lg p-6 hover:bg-[#1a1a1a]/80 transition-colors cursor-pointer"
                  onClick={() => setSelectedBlock(block)}
                >
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center space-x-4">
                      <div className="bg-[#1db954]/20 rounded-lg p-3">
                        <Hash className="text-[#1db954]" size={24} />
                      </div>
                      <div>
                        <h3 className="text-lg font-semibold">Bloque #{block.index}</h3>
                        <p className="text-sm text-gray-400">{formatTimestamp(block.timestamp)}</p>
                      </div>
                    </div>
                    {!isGenesis && blockData.rating !== 'N/A' && (
                      <div className="text-right">
                        <p className="text-sm text-gray-400">Valoración</p>
                        <p className="text-xl font-bold text-yellow-400">
                          {'★'.repeat(parseInt(blockData.rating) || 0)}
                        </p>
                      </div>
                    )}
                  </div>

                  <div className="space-y-2">
                    {isGenesis ? (
                      <p className="text-[#1db954] font-semibold">{blockData.content}</p>
                    ) : (
                      <div className="grid grid-cols-2 gap-4 text-sm">
                        <div>
                          <span className="text-gray-400">Usuario ID:</span>
                          <span className="ml-2">{blockData.userId}</span>
                        </div>
                        <div>
                          <span className="text-gray-400">Película ID:</span>
                          <span className="ml-2">{blockData.movieId}</span>
                        </div>
                      </div>
                    )}
                    
                    <div className="mt-4 pt-4 border-t border-white/10">
                      <p className="text-xs text-gray-400 font-mono break-all">
                        Hash: {block.hash}
                      </p>
                    </div>
                  </div>

                  {index < blocks.length - 1 && (
                    <div className="flex justify-center mt-4">
                      <LinkIcon className="text-gray-600 rotate-90" size={20} />
                    </div>
                  )}
                </motion.div>
              );
            })}
          </div>

          {/* Block Detail Modal */}
          <AnimatePresence>
            {selectedBlock && !showJson && (
              <>
                {/* Overlay */}
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  className="fixed inset-0 bg-black/80 z-40"
                  onClick={() => setSelectedBlock(null)}
                />
                
                {/* Modal Content */}
                <motion.div
                  initial={{ opacity: 0, scale: 0.9 }}
                  animate={{ opacity: 1, scale: 1 }}
                  exit={{ opacity: 0, scale: 0.9 }}
                  className="fixed inset-0 flex items-center justify-center p-4 z-50 pointer-events-none"
                >
                  <div 
                    className="bg-[#1a1a1a] rounded-xl p-6 max-w-2xl w-full max-h-[80vh] overflow-y-auto pointer-events-auto"
                    onClick={e => e.stopPropagation()}
                  >
                    <div className="flex items-center justify-between mb-6">
                      <h3 className="text-2xl font-bold">Bloque #{selectedBlock.index}</h3>
                      <button
                        onClick={() => setSelectedBlock(null)}
                        className="text-gray-400 hover:text-white transition-colors p-2 hover:bg-white/10 rounded-lg"
                      >
                        <X size={24} />
                      </button>
                    </div>

                    <div className="space-y-4">
                      <div className="bg-black/20 rounded-lg p-4">
                        <p className="text-sm text-gray-400 mb-1">Timestamp</p>
                        <p className="font-mono">{formatTimestamp(selectedBlock.timestamp)}</p>
                      </div>

                      <div className="bg-black/20 rounded-lg p-4">
                        <p className="text-sm text-gray-400 mb-1">Datos</p>
                        <p className="font-mono text-sm break-all">{selectedBlock.data}</p>
                      </div>

                      <div className="bg-black/20 rounded-lg p-4">
                        <p className="text-sm text-gray-400 mb-1">Hash del Bloque</p>
                        <p className="font-mono text-xs break-all text-[#1db954]">{selectedBlock.hash}</p>
                      </div>

                      <div className="bg-black/20 rounded-lg p-4">
                        <p className="text-sm text-gray-400 mb-1">Hash Anterior</p>
                        <p className="font-mono text-xs break-all">{selectedBlock.previousHash}</p>
                      </div>

                      <div className="bg-black/20 rounded-lg p-4">
                        <p className="text-sm text-gray-400 mb-1">Nonce (Proof of Work)</p>
                        <p className="font-mono text-2xl">{selectedBlock.nonce}</p>
                      </div>

                      <div className="flex justify-end mt-6">
                        <button
                          onClick={() => setShowJson(true)}
                          className="flex items-center space-x-2 px-4 py-2 bg-[#1db954]/10 text-[#1db954] rounded-lg hover:bg-[#1db954]/20 transition-colors"
                        >
                          <Hash size={16} />
                          <span>Ver JSON</span>
                        </button>
                      </div>
                    </div>
                  </div>
                </motion.div>
              </>
            )}

            {/* JSON Viewer Modal */}
            {showJson && selectedBlock && (
              <>
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  className="fixed inset-0 bg-black/80 z-60"
                  onClick={() => setShowJson(false)}
                />
                <motion.div
                  initial={{ opacity: 0, scale: 0.9 }}
                  animate={{ opacity: 1, scale: 1 }}
                  exit={{ opacity: 0, scale: 0.9 }}
                  className="fixed inset-0 flex items-center justify-center p-4 z-60 pointer-events-none"
                >
                  <div
                    className="bg-[#1a1a1a] rounded-xl w-full max-w-3xl max-h-[85vh] overflow-hidden pointer-events-auto"
                    onClick={e => e.stopPropagation()}
                  >
                    {/* Header */}
                    <div className="flex items-center justify-between p-6 border-b border-white/10">
                      <h4 className="text-xl font-bold">JSON del Bloque #{selectedBlock.index}</h4>
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={copyToClipboard}
                          className="flex items-center space-x-2 px-3 py-2 bg-[#1db954]/10 text-[#1db954] rounded-lg hover:bg-[#1db954]/20 transition-colors"
                        >
                          {copied ? (
                            <>
                              <Check size={16} />
                              <span>Copiado</span>
                            </>
                          ) : (
                            <>
                              <Copy size={16} />
                              <span>Copiar</span>
                            </>
                          )}
                        </button>
                        <button
                          onClick={() => setShowJson(false)}
                          className="text-gray-400 hover:text-white transition-colors p-2 hover:bg-white/10 rounded-lg"
                        >
                          <X size={20} />
                        </button>
                      </div>
                    </div>

                    {/* JSON Content */}
                    <div className="overflow-auto max-h-[calc(85vh-100px)] p-6">
                      <div className="bg-black/20 rounded-lg p-4">
                        <pre className="text-sm text-gray-300 whitespace-pre-wrap font-mono leading-relaxed">
                          {JSON.stringify(selectedBlock, null, 2)}
                        </pre>
                      </div>
                    </div>
                  </div>
                </motion.div>
              </>
            )}
          </AnimatePresence>
        </div>
      </main>
    </div>
  );
}